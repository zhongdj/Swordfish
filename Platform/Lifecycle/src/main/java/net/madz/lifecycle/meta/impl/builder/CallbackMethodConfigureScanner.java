package net.madz.lifecycle.meta.impl.builder;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;

import net.madz.lifecycle.SyntaxErrors;
import net.madz.lifecycle.annotations.Null;
import net.madz.lifecycle.annotations.callback.AnyState;
import net.madz.lifecycle.annotations.callback.Callbacks;
import net.madz.lifecycle.annotations.callback.PostStateChange;
import net.madz.lifecycle.annotations.callback.PreStateChange;
import net.madz.lifecycle.annotations.relation.Relation;
import net.madz.lifecycle.meta.MetaObject.ReadAccessor;
import net.madz.lifecycle.meta.impl.builder.ObjectBuilderBase.MethodScanner;
import net.madz.lifecycle.meta.instance.RelationObject;
import net.madz.lifecycle.meta.instance.StateMachineObject;
import net.madz.lifecycle.meta.template.RelationMetadata;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.util.StringUtil;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailureSet;

final class CallbackMethodConfigureScanner<S> implements MethodScanner {

    /**
     * 
     */
    private final StateMachineObjectBuilderImpl<S> stateMachineObjectBuilderImpl;
    private final Class<?> klass;
    private final HashSet<String> lifecycleOverridenCallbackDefinitionSet = new HashSet<>();

    public CallbackMethodConfigureScanner(StateMachineObjectBuilderImpl<S> stateMachineObjectBuilderImpl, Class<?> klass) {
        this.stateMachineObjectBuilderImpl = stateMachineObjectBuilderImpl;
        this.klass = klass;
    }

    @Override
    public boolean onMethodFound(Method method, VerificationFailureSet failureSet) {
        if ( !isCallbackMethod(method) ) {
            return false;
        }
        if ( lifecycleOverridenCallbackDefinitionSet.contains(method.getName()) ) {
            return false;
        }
        try {
            final Callbacks callbacks = method.getAnnotation(Callbacks.class);
            if ( null != callbacks ) {
                configureCallbacks(method, callbacks);
            }
            final PreStateChange preStateChange = method.getAnnotation(PreStateChange.class);
            if ( null != preStateChange ) {
                configurePreStateChange(method, preStateChange);
            }
            final PostStateChange postStateChange = method.getAnnotation(PostStateChange.class);
            if ( null != postStateChange ) {
                configurePostStateChange(method, postStateChange);
            }
            if ( stateMachineObjectBuilderImpl.hasOverrides(method) ) {
                lifecycleOverridenCallbackDefinitionSet.add(method.getName());
            }
        } catch (VerificationException e) {
            failureSet.add(e);
        }
        return false;
    }

    private void configurePostStateChange(Method method, PostStateChange postStateChange) throws VerificationException {
        final Class<?> from = postStateChange.from();
        final Class<?> to = postStateChange.to();
        final String relation = postStateChange.observableName();
        final String mappedBy = postStateChange.mappedBy();
        if ( PostStateChange.NULL_STR.equals(relation) ) {
            configurePostStateChangeNonRelationalCallbackObjects(method, from, to, this.stateMachineObjectBuilderImpl.specificPostStateChangeCallbackObjects,
                    this.stateMachineObjectBuilderImpl.commonPostStateChangeCallbackObjects);
        } else {
            final Class<?> observableClass = getRelationObjectClass(klass, relation);
            if ( null == observableClass ) {
                throw this.stateMachineObjectBuilderImpl.newVerificationException(this.stateMachineObjectBuilderImpl.getDottedPath(),
                        SyntaxErrors.POST_STATE_CHANGE_RELATION_INVALID, relation, method, klass);
            }
            final StateMachineObject<?> callBackEventSourceContainer = this.stateMachineObjectBuilderImpl.getRegistry().loadStateMachineObject(observableClass);
            String convertRelationKey = convertRelationKey(observableClass, mappedBy);
            if ( null == convertRelationKey ) {
                throw this.stateMachineObjectBuilderImpl.newVerificationException(callBackEventSourceContainer.getDottedPath(),
                        SyntaxErrors.POST_STATE_CHANGE_MAPPEDBY_INVALID, mappedBy, method, observableClass);
            }
            ReadAccessor<?> accessor = null;
            accessor = evaluateAccessor(convertRelationKey, callBackEventSourceContainer);
            configurePostStateChangeCallbackObjectsWithRelational(method, from, to, callBackEventSourceContainer, accessor);
        }
    }

    private String convertRelationKey(Class<?> klz, String mappedBy) {
        Field observerField = null;
        for ( Class<?> clz = klz; clz != Object.class; clz = clz.getSuperclass() ) {
            try {
                observerField = clz.getDeclaredField(mappedBy);
                break;
            } catch (NoSuchFieldException | SecurityException e) {
                continue;
            }
        }
        if ( null != observerField && null != observerField.getAnnotation(Relation.class) ) {
            final Relation relation = observerField.getAnnotation(Relation.class);
            if ( Null.class != relation.value() ) {
                return relation.value().getName();
            } else {
                return StringUtil.toUppercaseFirstCharacter(mappedBy);
            }
        }
        Method observerMethod = null;
        for ( Class<?> clz = klz; clz != Object.class; clz = clz.getSuperclass() ) {
            try {
                observerMethod = clz.getDeclaredMethod("get" + StringUtil.toUppercaseFirstCharacter(mappedBy));
                break;
            } catch (NoSuchMethodException | SecurityException e) {
                continue;
            }
        }
        if ( null != observerMethod ) {
            Relation relation = observerMethod.getAnnotation(Relation.class);
            if ( null != relation ) {
                if ( Null.class != relation.value() ) {
                    return relation.value().getName();
                } else {
                    return StringUtil.toUppercaseFirstCharacter(mappedBy);
                }
            }
        }
        return null;
    }

    private void configurePreStateChange(Method method, PreStateChange preStateChange) throws VerificationException {
        final Class<?> from = preStateChange.from();
        final Class<?> to = preStateChange.to();
        final String relation = preStateChange.observableName();
        final String mappedBy = preStateChange.mappedBy();
        if ( PreStateChange.NULL_STR.equals(relation) ) {
            configurePreStateChangeNonRelationalCallbackObjects(method, from, to, this.stateMachineObjectBuilderImpl.specificPreStateChangeCallbackObjects,
                    this.stateMachineObjectBuilderImpl.commonPreStateChangeCallbackObjects);
        } else {
            Class<?> observableClass = getRelationObjectClass(klass, relation);
            if ( null == observableClass ) {
                throw this.stateMachineObjectBuilderImpl.newVerificationException(this.stateMachineObjectBuilderImpl.getDottedPath(),
                        SyntaxErrors.PRE_STATE_CHANGE_RELATION_INVALID, relation, method, klass);
            }
            final StateMachineObject<?> callBackEventSourceContainer = this.stateMachineObjectBuilderImpl.getRegistry().loadStateMachineObject(observableClass);
            String convertRelationKey = convertRelationKey(observableClass, mappedBy);
            if ( null == convertRelationKey ) {
                throw this.stateMachineObjectBuilderImpl.newVerificationException(callBackEventSourceContainer.getDottedPath(),
                        SyntaxErrors.PRE_STATE_CHANGE_MAPPEDBY_INVALID, mappedBy, method, observableClass);
            }
            final ReadAccessor<?> accessor = evaluateAccessor(mappedBy, callBackEventSourceContainer);
            configurePreStateChangeRelationalCallbackObjects(method, from, to, callBackEventSourceContainer, accessor);
        }
    }

    private void configurePreStateChangeRelationalCallbackObjects(Method method, Class<?> from, Class<?> to,
            final StateMachineObject<?> callBackEventSourceContainer, final ReadAccessor<?> accessor) {
        RelationalCallbackObject item = null;
        if ( AnyState.class != from && AnyState.class != to ) {
            item = new RelationalCallbackObject(from.getSimpleName(), to.getSimpleName(), method, accessor);
            callBackEventSourceContainer.addSpecificPreStateChangeCallbackObject(item);
        } else if ( AnyState.class == from && AnyState.class != to ) {
            item = new RelationalCallbackObject(AnyState.class.getSimpleName(), to.getSimpleName(), method, accessor);
            callBackEventSourceContainer.getState(to).addPreToCallbackObject(to, item);
        } else if ( AnyState.class != from && AnyState.class == to ) {
            item = new RelationalCallbackObject(AnyState.class.getSimpleName(), from.getSimpleName(), method, accessor);
            callBackEventSourceContainer.getState(from).addPreFromCallbackObject(from, item);
        } else {
            item = new RelationalCallbackObject(AnyState.class.getSimpleName(), AnyState.class.getSimpleName(), method, accessor);
            callBackEventSourceContainer.addCommonPreStateChangeCallbackObject(item);
        }
    }

    private void configurePostStateChangeCallbackObjectsWithRelational(Method method, Class<?> from, Class<?> to,
            final StateMachineObject<?> callBackEventSourceContainer, final ReadAccessor<?> accessor) {
        RelationalCallbackObject item = null;
        if ( AnyState.class != from && AnyState.class != to ) {
            item = new RelationalCallbackObject(from.getSimpleName(), to.getSimpleName(), method, accessor);
            callBackEventSourceContainer.addSpecificPostStateChangeCallbackObject(item);
        } else if ( AnyState.class == from && AnyState.class != to ) {
            item = new RelationalCallbackObject(AnyState.class.getSimpleName(), to.getSimpleName(), method, accessor);
            callBackEventSourceContainer.getState(to).addPostToCallbackObject(to, item);
        } else if ( AnyState.class != from && AnyState.class == to ) {
            item = new RelationalCallbackObject(AnyState.class.getSimpleName(), from.getSimpleName(), method, accessor);
            callBackEventSourceContainer.getState(from).addPostFromCallbackObject(from, item);
        } else {
            item = new RelationalCallbackObject(AnyState.class.getSimpleName(), AnyState.class.getSimpleName(), method, accessor);
            callBackEventSourceContainer.addCommonPostStateChangeCallbackObject(item);
        }
    }

    private ReadAccessor<?> evaluateAccessor(String relationKey, final StateMachineObject<?> callBackEventSourceContainer) {
        final StateMachineMetadata callBackEventSourceStateMachineMetadat = (StateMachineMetadata) callBackEventSourceContainer.getMetaType();
        final RelationMetadata mappedByRelationMetadata = callBackEventSourceStateMachineMetadat.getRelationMetadata(relationKey);
        final RelationObject relationObject = callBackEventSourceContainer.getRelationObject(mappedByRelationMetadata.getPrimaryKey());
        ReadAccessor<?> accessor = relationObject.getEvaluator();
        return accessor;
    }

    private Class<?> getRelationObjectClass(Class<?> klass, String relation) {
        Field declaredField = null;
        for ( Class<?> clz = klass; clz != Object.class; clz = clz.getSuperclass() ) {
            try {
                declaredField = clz.getDeclaredField(relation);
                break;
            } catch (NoSuchFieldException | SecurityException e) {
                continue;
            }
        }
        if ( null != declaredField ) {
            Type genericType = declaredField.getGenericType();
            if ( genericType instanceof ParameterizedType ) {
                ParameterizedType pType = (ParameterizedType) genericType;
                return (Class<?>) pType.getActualTypeArguments()[0];
            }
            return declaredField.getType();
        }
        Method relatedMethod = null;
        for ( Class<?> clz = klass; clz != Object.class; clz = clz.getSuperclass() ) {
            try {
                relatedMethod = clz.getDeclaredMethod("get" + StringUtil.toUppercaseFirstCharacter(relation));
                break;
            } catch (NoSuchMethodException | SecurityException e) {
                continue;
            }
        }
        if ( null != relatedMethod ) {
            Class<?> relatedClass = relatedMethod.getReturnType();
            if ( relatedClass.isArray() ) {
                return relatedClass.getComponentType();
            } else if ( Iterable.class.isAssignableFrom(relatedClass) ) {
                return null;
            } else {
                return relatedClass;
            }
        }
        return null;
    }

    private void configurePreStateChangeNonRelationalCallbackObjects(Method method, Class<?> from, Class<?> to,
            final ArrayList<CallbackObject> specificCallbackObjects, final ArrayList<CallbackObject> commonCallbackObjects) {
        CallbackObject item = null;
        if ( AnyState.class != from && AnyState.class != to ) {
            item = new CallbackObject(from.getSimpleName(), to.getSimpleName(), method);
            specificCallbackObjects.add(item);
        } else if ( AnyState.class == from && AnyState.class != to ) {
            item = new CallbackObject(AnyState.class.getSimpleName(), to.getSimpleName(), method);
            this.stateMachineObjectBuilderImpl.getState(to).addPreToCallbackObject(to, item);
        } else if ( AnyState.class != from && AnyState.class == to ) {
            item = new CallbackObject(AnyState.class.getSimpleName(), from.getSimpleName(), method);
            this.stateMachineObjectBuilderImpl.getState(from).addPreFromCallbackObject(from, item);
        } else {
            item = new CallbackObject(AnyState.class.getSimpleName(), AnyState.class.getSimpleName(), method);
            commonCallbackObjects.add(item);
        }
    }

    private void configurePostStateChangeNonRelationalCallbackObjects(Method method, Class<?> from, Class<?> to,
            final ArrayList<CallbackObject> specificCallbackObjects, final ArrayList<CallbackObject> commonCallbackObjects) {
        CallbackObject item = null;
        if ( AnyState.class != from && AnyState.class != to ) {
            item = new CallbackObject(from.getSimpleName(), to.getSimpleName(), method);
            specificCallbackObjects.add(item);
        } else if ( AnyState.class == from && AnyState.class != to ) {
            item = new CallbackObject(AnyState.class.getSimpleName(), to.getSimpleName(), method);
            this.stateMachineObjectBuilderImpl.getState(to).addPostToCallbackObject(to, item);
        } else if ( AnyState.class != from && AnyState.class == to ) {
            item = new CallbackObject(AnyState.class.getSimpleName(), from.getSimpleName(), method);
            this.stateMachineObjectBuilderImpl.getState(from).addPostFromCallbackObject(from, item);
        } else {
            item = new CallbackObject(AnyState.class.getSimpleName(), AnyState.class.getSimpleName(), method);
            commonCallbackObjects.add(item);
        }
    }

    private void configureCallbacks(Method method, Callbacks callbacks) throws VerificationException {
        final PreStateChange[] preStateChange = callbacks.preStateChange();
        for ( final PreStateChange item : preStateChange ) {
            configurePreStateChange(method, item);
        }
        final PostStateChange[] postStateChange = callbacks.postStateChange();
        for ( final PostStateChange item : postStateChange ) {
            configurePostStateChange(method, item);
        }
    }

    boolean hasAnnotation(AnnotatedElement element, Class<? extends Annotation> annotationClass) {
        return null != element.getAnnotation(annotationClass);
    }

    private boolean isCallbackMethod(Method method) {
        if ( hasAnnotation(method, Callbacks.class) || hasAnnotation(method, PreStateChange.class) || hasAnnotation(method, PostStateChange.class) ) {
            return true;
        }
        return false;
    }
}