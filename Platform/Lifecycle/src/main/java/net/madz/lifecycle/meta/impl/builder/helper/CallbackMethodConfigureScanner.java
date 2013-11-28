package net.madz.lifecycle.meta.impl.builder.helper;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;

import net.madz.lifecycle.SyntaxErrors;
import net.madz.lifecycle.annotations.Null;
import net.madz.lifecycle.annotations.callback.AnyState;
import net.madz.lifecycle.annotations.callback.CallbackConsts;
import net.madz.lifecycle.annotations.callback.Callbacks;
import net.madz.lifecycle.annotations.callback.PostStateChange;
import net.madz.lifecycle.annotations.callback.PreStateChange;
import net.madz.lifecycle.annotations.relation.Relation;
import net.madz.lifecycle.meta.FieldEvaluator;
import net.madz.lifecycle.meta.MetaObject.ReadAccessor;
import net.madz.lifecycle.meta.PropertyEvaluator;
import net.madz.lifecycle.meta.impl.builder.CallbackObject;
import net.madz.lifecycle.meta.impl.builder.RelationalCallbackObject;
import net.madz.lifecycle.meta.impl.builder.StateMachineObjectBuilderImpl;
import net.madz.lifecycle.meta.instance.StateMachineObject;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.lifecycle.meta.template.TransitionMetadata;
import net.madz.util.StringUtil;
import net.madz.verification.VerificationException;

public final class CallbackMethodConfigureScanner {

    private final StateMachineObjectBuilderImpl<?> stateMachineObjectBuilderImpl;
    private final Class<?> klass;
    private final HashSet<String> lifecycleOverridenCallbackDefinitionSet = new HashSet<>();

    public CallbackMethodConfigureScanner(StateMachineObjectBuilderImpl<?> stateMachineObjectBuilderImpl, Class<?> klass) {
        this.stateMachineObjectBuilderImpl = stateMachineObjectBuilderImpl;
        this.klass = klass;
    }

    public void scanMethod() throws VerificationException {
        if ( klass.isInterface() ) {
            return;
        }
        for ( Class<?> clazz = klass; clazz != null && clazz != Object.class; clazz = clazz.getSuperclass() ) {
            for ( Method method : clazz.getDeclaredMethods() ) {
                onMethodFound(method);
            }
        }
    }

    public boolean onMethodFound(Method method) throws VerificationException {
        if ( !isCallbackMethod(method) ) {
            return false;
        }
        if ( lifecycleOverridenCallbackDefinitionSet.contains(method.getName()) ) {
            return false;
        }
        configureCallbacks(method, method.getAnnotation(Callbacks.class));
        configurePreStateChange(method, method.getAnnotation(PreStateChange.class));
        configurePostStateChange(method, method.getAnnotation(PostStateChange.class));
        if ( stateMachineObjectBuilderImpl.hasOverrides(method) ) {
            lifecycleOverridenCallbackDefinitionSet.add(method.getName());
        }
        return false;
    }

    private void configureCallbacks(final Method method, final Callbacks callbacks) throws VerificationException {
        if ( null == callbacks ) {
            return;
        }
        final PreStateChange[] preStateChange = callbacks.preStateChange();
        for ( final PreStateChange item : preStateChange ) {
            configurePreStateChange(method, item);
        }
        final PostStateChange[] postStateChange = callbacks.postStateChange();
        for ( final PostStateChange item : postStateChange ) {
            configurePostStateChange(method, item);
        }
    }

    private void configurePreStateChange(final Method method, final PreStateChange preStateChange) throws VerificationException {
        if ( null == preStateChange ) {
            return;
        }
        final Class<?> from = preStateChange.from();
        final Class<?> to = preStateChange.to();
        final String observableName = preStateChange.observableName();
        final String mappedBy = preStateChange.mappedBy();
        final Class<?> observableClass = preStateChange.observableClass();
        if ( isNonRelationalCallback(observableName, observableClass) ) {
            configurePreStateChangeNonRelationalCallbackObjects(method, from, to);
        } else {
            final Class<?> actualObservableClass = evaluateActualObservableClassOfPreStateChange(method, observableName, observableClass);
            final StateMachineObject<?> callBackEventSourceContainer = this.stateMachineObjectBuilderImpl.getRegistry().loadStateMachineObject(
                    actualObservableClass);
            if ( AnyState.class != to ) {
                verifyPreToState(method, to, callBackEventSourceContainer.getMetaType());
                verifyPreToStatePostEvaluate(method, to, callBackEventSourceContainer.getMetaType());
            }
            if ( AnyState.class != from ) {
                verifyPreFromState(method, from, callBackEventSourceContainer.getMetaType());
            }
            validateMappedbyOfPreStateChange(method, mappedBy, actualObservableClass);
            final ReadAccessor<?> accessor = evaluateAccessor(mappedBy, actualObservableClass);
            configurePreStateChangeRelationalCallbackObjects(method, from, to, callBackEventSourceContainer, accessor);
        }
    }

    private void validateMappedbyOfPreStateChange(final Method method, final String mappedBy, final Class<?> actualObservableClass)
            throws VerificationException {
        final StateMachineObject<?> callBackEventSourceContainer = this.stateMachineObjectBuilderImpl.getRegistry().loadStateMachineObject(
                actualObservableClass);
        if ( null == mappedBy || CallbackConsts.NULL_STR.equalsIgnoreCase(mappedBy) ) {
            throw this.stateMachineObjectBuilderImpl.newVerificationException(this.stateMachineObjectBuilderImpl.getDottedPath(),
                    SyntaxErrors.PRE_STATE_CHANGE_MAPPEDBY_INVALID, mappedBy, method, actualObservableClass);
        }
        final String convertRelationKey = convertRelationKey(actualObservableClass, mappedBy);
        if ( null == convertRelationKey ) {
            throw this.stateMachineObjectBuilderImpl.newVerificationException(callBackEventSourceContainer.getDottedPath(),
                    SyntaxErrors.PRE_STATE_CHANGE_MAPPEDBY_INVALID, mappedBy, method, actualObservableClass);
        }
    }

    private Class<?> evaluateActualObservableClassOfPreStateChange(final Method method, final String observableName, final Class<?> observableClass)
            throws VerificationException {
        Class<?> actualObservableClass = null;
        if ( !CallbackConsts.NULL_STR.equals(observableName) && Null.class != observableClass ) {
            verifyObservableClass(method, observableClass, SyntaxErrors.PRE_STATE_CHANGE_OBSERVABLE_CLASS_INVALID);
            final Class<?> observableClassViaObservaleName = verifyObservableName(method, observableName, SyntaxErrors.PRE_STATE_CHANGE_RELATION_INVALID);
            if ( !observableClass.isAssignableFrom(observableClassViaObservaleName) ) {
                throw this.stateMachineObjectBuilderImpl.newVerificationException(this.stateMachineObjectBuilderImpl.getDottedPath(),
                        SyntaxErrors.PRE_STATE_CHANGE_OBSERVABLE_NAME_MISMATCH_OBSERVABLE_CLASS, observableName, observableClass, method);
            }
            actualObservableClass = observableClass;
        } else if ( CallbackConsts.NULL_STR.equals(observableName) && Null.class != observableClass ) {
            verifyObservableClass(method, observableClass, SyntaxErrors.PRE_STATE_CHANGE_OBSERVABLE_CLASS_INVALID);
            actualObservableClass = observableClass;
        } else if ( !CallbackConsts.NULL_STR.equals(observableName) && Null.class == observableClass ) {
            actualObservableClass = verifyObservableName(method, observableName, SyntaxErrors.PRE_STATE_CHANGE_RELATION_INVALID);
        }
        return actualObservableClass;
    }

    private boolean isNonRelationalCallback(final String observableName, final Class<?> observableClass) {
        return CallbackConsts.NULL_STR.equals(observableName) && Null.class == observableClass;
    }

    private void configurePostStateChange(Method method, PostStateChange postStateChange) throws VerificationException {
        if ( null == postStateChange ) {
            return;
        }
        final Class<?> from = postStateChange.from();
        final Class<?> to = postStateChange.to();
        final String observableName = postStateChange.observableName();
        final String mappedBy = postStateChange.mappedBy();
        final Class<?> observableClass = postStateChange.observableClass();
        if ( isNonRelationalCallback(observableName, observableClass) ) {
            configurePostStateChangeNonRelationalCallbackObjects(method, from, to);
        } else {
            final Class<?> actualObservableClass = evaluateActualObservableClassOfPostStateChange(method, observableName, observableClass);
            validateMappedbyOfPostStateChange(method, mappedBy, actualObservableClass);
            final StateMachineObject<?> callBackEventSourceContainer = this.stateMachineObjectBuilderImpl.getRegistry().loadStateMachineObject(
                    actualObservableClass);
            if ( AnyState.class != from ) {
                verifyPostFromState(method, from, callBackEventSourceContainer.getMetaType());
            }
            if ( AnyState.class != to ) {
                verifyPostToState(method, to, callBackEventSourceContainer.getMetaType());
            }
            final ReadAccessor<?> accessor = evaluateAccessor(mappedBy, actualObservableClass);
            configurePostStateChangeCallbackObjectsWithRelational(method, from, to, callBackEventSourceContainer, accessor);
        }
    }

    private void validateMappedbyOfPostStateChange(Method method, final String mappedBy, final Class<?> actualObservableClass) throws VerificationException {
        final StateMachineObject<?> callBackEventSourceContainer = this.stateMachineObjectBuilderImpl.getRegistry().loadStateMachineObject(
                actualObservableClass);
        if ( null == mappedBy || CallbackConsts.NULL_STR.equalsIgnoreCase(mappedBy) ) {
            throw this.stateMachineObjectBuilderImpl.newVerificationException(this.stateMachineObjectBuilderImpl.getDottedPath(),
                    SyntaxErrors.POST_STATE_CHANGE_MAPPEDBY_INVALID, mappedBy, method, actualObservableClass);
        }
        final String convertRelationKey = convertRelationKey(actualObservableClass, mappedBy);
        if ( null == convertRelationKey ) {
            throw this.stateMachineObjectBuilderImpl.newVerificationException(callBackEventSourceContainer.getDottedPath(),
                    SyntaxErrors.POST_STATE_CHANGE_MAPPEDBY_INVALID, mappedBy, method, actualObservableClass);
        }
    }

    private Class<?> evaluateActualObservableClassOfPostStateChange(Method method, final String observableName, final Class<?> observableClass)
            throws VerificationException {
        Class<?> actualObservableClass = null;
        if ( !CallbackConsts.NULL_STR.equals(observableName) && Null.class != observableClass ) {
            verifyObservableClass(method, observableClass, SyntaxErrors.POST_STATE_CHANGE_OBSERVABLE_CLASS_INVALID);
            final Class<?> observableClassViaObservaleName = verifyObservableName(method, observableName, SyntaxErrors.POST_STATE_CHANGE_RELATION_INVALID);
            if ( !observableClass.isAssignableFrom(observableClassViaObservaleName) ) {
                throw this.stateMachineObjectBuilderImpl.newVerificationException(this.stateMachineObjectBuilderImpl.getDottedPath(),
                        SyntaxErrors.POST_STATE_CHANGE_OBSERVABLE_NAME_MISMATCH_OBSERVABLE_CLASS, observableName, observableClass, method);
            }
            actualObservableClass = observableClass;
        } else if ( CallbackConsts.NULL_STR.equals(observableName) && Null.class != observableClass ) {
            verifyObservableClass(method, observableClass, SyntaxErrors.POST_STATE_CHANGE_OBSERVABLE_CLASS_INVALID);
            actualObservableClass = observableClass;
        } else if ( !CallbackConsts.NULL_STR.equals(observableName) && Null.class == observableClass ) {
            actualObservableClass = verifyObservableName(method, observableName, SyntaxErrors.POST_STATE_CHANGE_RELATION_INVALID);
        }
        return actualObservableClass;
    }

    private Class<?> verifyObservableName(Method method, final String observableName, String errorCode) throws VerificationException {
        Class<?> actualObservableClass;
        actualObservableClass = evaluateObservableClass(klass, observableName);
        if ( null == actualObservableClass ) {
            throw this.stateMachineObjectBuilderImpl.newVerificationException(this.stateMachineObjectBuilderImpl.getDottedPath(), errorCode, observableName,
                    method, klass);
        }
        return actualObservableClass;
    }

    private void verifyObservableClass(Method method, final Class<?> observableClass, String errorCode) throws VerificationException {
        try {
            this.stateMachineObjectBuilderImpl.getRegistry().loadStateMachineObject(observableClass);
        } catch (VerificationException e) {
            if ( e.getVerificationFailureSet().iterator().next().getErrorCode().equals(SyntaxErrors.REGISTERED_META_ERROR) ) {
                throw this.stateMachineObjectBuilderImpl.newVerificationException(this.stateMachineObjectBuilderImpl.getDottedPath(), errorCode,
                        observableClass, method);
            }
        }
    }

    private void verifyPostToState(final Method method, Class<?> to, final StateMachineMetadata metaType) throws VerificationException {
        if ( null == metaType.getState(to) ) {
            throw this.stateMachineObjectBuilderImpl.newVerificationException(metaType.getDottedPath(), SyntaxErrors.POST_STATE_CHANGE_TO_STATE_IS_INVALID, to,
                    method, metaType.getPrimaryKey());
        }
    }

    private void verifyPostFromState(Method method, Class<?> from, StateMachineMetadata metaType) throws VerificationException {
        if ( null == metaType.getState(from) ) {
            throw this.stateMachineObjectBuilderImpl.newVerificationException(metaType.getDottedPath(), SyntaxErrors.POST_STATE_CHANGE_FROM_STATE_IS_INVALID,
                    from, method, metaType.getPrimaryKey());
        }
    }

    private String convertRelationKey(Class<?> klass, String mappedBy) {
        Field observerField = evaluateObserverField(klass, mappedBy);
        if ( isRightRelationField(observerField) ) {
            return evaluateRelationKeyFromRelationField(mappedBy, observerField);
        }
        Method observerMethod = evaluateObserverMethod(klass, mappedBy);
        if ( null == observerMethod ) {
            return null;
        }
        final Relation relation = observerMethod.getAnnotation(Relation.class);
        if ( null == relation ) {
            return null;
        }
        if ( Null.class != relation.value() ) {
            return relation.value().getName();
        } else {
            return StringUtil.toUppercaseFirstCharacter(mappedBy);
        }
    }

    private Method evaluateObserverMethod(Class<?> klass, String mappedBy) {
        Method observerMethod = null;
        for ( Class<?> clazz = klass; clazz != Object.class; clazz = clazz.getSuperclass() ) {
            try {
                observerMethod = clazz.getDeclaredMethod("get" + StringUtil.toUppercaseFirstCharacter(mappedBy));
                break;
            } catch (NoSuchMethodException | SecurityException e) {
                continue;
            }
        }
        return observerMethod;
    }

    private String evaluateRelationKeyFromRelationField(String mappedBy, Field observerField) {
        final Relation relation = observerField.getAnnotation(Relation.class);
        if ( Null.class != relation.value() ) {
            return relation.value().getName();
        } else {
            return StringUtil.toUppercaseFirstCharacter(mappedBy);
        }
    }

    private boolean isRightRelationField(Field observerField) {
        return null != observerField && null != observerField.getAnnotation(Relation.class);
    }

    private Field evaluateObserverField(Class<?> klass, String mappedBy) {
        Field observerField = null;
        for ( Class<?> clazz = klass; clazz != Object.class; clazz = clazz.getSuperclass() ) {
            try {
                observerField = clazz.getDeclaredField(mappedBy);
                break;
            } catch (NoSuchFieldException | SecurityException e) {
                continue;
            }
        }
        return observerField;
    }

    private void verifyPreToState(Method method, Class<?> to, StateMachineMetadata metaType) throws VerificationException {
        if ( null == metaType.getState(to) ) {
            throw this.stateMachineObjectBuilderImpl.newVerificationException(metaType.getDottedPath(), SyntaxErrors.PRE_STATE_CHANGE_TO_STATE_IS_INVALID, to,
                    method, metaType.getPrimaryKey());
        }
    }

    private void verifyPreFromState(Method method, Class<?> from, StateMachineMetadata metaType) throws VerificationException {
        if ( null == metaType.getState(from) ) {
            throw this.stateMachineObjectBuilderImpl.newVerificationException(metaType.getDottedPath(), SyntaxErrors.PRE_STATE_CHANGE_FROM_STATE_IS_INVALID,
                    from, method, metaType.getPrimaryKey());
        }
    }

    private void verifyPreToStatePostEvaluate(Method method, Class<?> toStateClass, StateMachineMetadata stateMachineMetadata) throws VerificationException {
        for ( final TransitionMetadata transition : stateMachineMetadata.getState(toStateClass).getPossibleReachingTransitions() ) {
            if ( transition.isConditional() && transition.postValidate() ) {
                throw this.stateMachineObjectBuilderImpl.newVerificationException(stateMachineMetadata.getDottedPath(),
                        SyntaxErrors.PRE_STATE_CHANGE_TO_POST_EVALUATE_STATE_IS_INVALID, toStateClass, method, transition.getDottedPath());
            }
        }
    }

    private ReadAccessor<?> evaluateAccessor(String mappedBy, Class<?> observableClass) {
        final Field observerField = findObserverField(mappedBy, observableClass);
        if ( observerField != null ) {
            return new FieldEvaluator<>(observerField);
        }
        final Method observerMethod = findObserverProperty(mappedBy, observableClass);
        if ( null != observerMethod ) {
            return new PropertyEvaluator<>(observerMethod);
        }
        return null;
    }

    private Method findObserverProperty(String mappedBy, Class<?> observableClass) {
        Method getter = null;
        for ( Class<?> klass = observableClass; klass != Object.class; klass = klass.getSuperclass() ) {
            try {
                getter = klass.getDeclaredMethod("get" + StringUtil.toUppercaseFirstCharacter(mappedBy));
                break;
            } catch (NoSuchMethodException | SecurityException e) {
                continue;
            }
        }
        return getter;
    }

    private Field findObserverField(String mappedBy, Class<?> observableClass) {
        Field observerField = null;
        for ( Class<?> klass = observableClass; klass != Object.class; klass = klass.getSuperclass() ) {
            try {
                observerField = klass.getDeclaredField(mappedBy);
                break;
            } catch (NoSuchFieldException | SecurityException e) {
                continue;
            }
        }
        return observerField;
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
            item = new RelationalCallbackObject(from.getSimpleName(), AnyState.class.getSimpleName(), method, accessor);
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
            item = new RelationalCallbackObject(from.getSimpleName(), AnyState.class.getSimpleName(), method, accessor);
            callBackEventSourceContainer.getState(from).addPostFromCallbackObject(from, item);
        } else {
            item = new RelationalCallbackObject(AnyState.class.getSimpleName(), AnyState.class.getSimpleName(), method, accessor);
            callBackEventSourceContainer.addCommonPostStateChangeCallbackObject(item);
        }
    }

    private Class<?> evaluateObservableClass(Class<?> klass, String observableName) {
        final Field observableField = findObservableField(klass, observableName);
        if ( null != observableField ) {
            final Type genericType = observableField.getGenericType();
            if ( genericType instanceof ParameterizedType ) {
                ParameterizedType pType = (ParameterizedType) genericType;
                return (Class<?>) pType.getActualTypeArguments()[0];
            }
            return observableField.getType();
        }
        final Method observableMethod = findObservableMethod(klass, observableName);
        if ( null == observableMethod ) {
            return null;
        }
        Class<?> relatedClass = observableMethod.getReturnType();
        if ( relatedClass.isArray() ) {
            return relatedClass.getComponentType();
        } else if ( Iterable.class.isAssignableFrom(relatedClass) ) {
            return null;
        } else {
            return relatedClass;
        }
    }

    private Method findObservableMethod(Class<?> klass, String observableName) {
        Method relatedMethod = null;
        for ( Class<?> clazz = klass; clazz != Object.class; clazz = clazz.getSuperclass() ) {
            try {
                relatedMethod = clazz.getDeclaredMethod("get" + StringUtil.toUppercaseFirstCharacter(observableName));
                break;
            } catch (NoSuchMethodException | SecurityException e) {
                continue;
            }
        }
        return relatedMethod;
    }

    private Field findObservableField(Class<?> klass, String observableName) {
        Field declaredField = null;
        for ( Class<?> clazz = klass; clazz != Object.class; clazz = clazz.getSuperclass() ) {
            try {
                declaredField = clazz.getDeclaredField(observableName);
                break;
            } catch (NoSuchFieldException | SecurityException e) {
                continue;
            }
        }
        return declaredField;
    }

    private void configurePreStateChangeNonRelationalCallbackObjects(final Method method, final Class<?> from, final Class<?> to) {
        CallbackObject item = null;
        if ( AnyState.class != from && AnyState.class != to ) {
            item = new CallbackObject(from.getSimpleName(), to.getSimpleName(), method);
            this.stateMachineObjectBuilderImpl.addSpecificPreStateChangeCallbackObject(item);
        } else if ( AnyState.class == from && AnyState.class != to ) {
            item = new CallbackObject(AnyState.class.getSimpleName(), to.getSimpleName(), method);
            this.stateMachineObjectBuilderImpl.getState(to).addPreToCallbackObject(to, item);
        } else if ( AnyState.class != from && AnyState.class == to ) {
            item = new CallbackObject(from.getSimpleName(), AnyState.class.getSimpleName(), method);
            this.stateMachineObjectBuilderImpl.getState(from).addPreFromCallbackObject(from, item);
        } else {
            item = new CallbackObject(AnyState.class.getSimpleName(), AnyState.class.getSimpleName(), method);
            this.stateMachineObjectBuilderImpl.addCommonPreStateChangeCallbackObject(item);
        }
    }

    private void configurePostStateChangeNonRelationalCallbackObjects(Method method, Class<?> from, Class<?> to) {
        CallbackObject item = null;
        if ( AnyState.class != from && AnyState.class != to ) {
            item = new CallbackObject(from.getSimpleName(), to.getSimpleName(), method);
            this.stateMachineObjectBuilderImpl.addSpecificPostStateChangeCallbackObject(item);
        } else if ( AnyState.class == from && AnyState.class != to ) {
            item = new CallbackObject(AnyState.class.getSimpleName(), to.getSimpleName(), method);
            this.stateMachineObjectBuilderImpl.getState(to).addPostToCallbackObject(to, item);
        } else if ( AnyState.class != from && AnyState.class == to ) {
            item = new CallbackObject(from.getSimpleName(), AnyState.class.getSimpleName(), method);
            this.stateMachineObjectBuilderImpl.getState(from).addPostFromCallbackObject(from, item);
        } else {
            item = new CallbackObject(AnyState.class.getSimpleName(), AnyState.class.getSimpleName(), method);
            this.stateMachineObjectBuilderImpl.addCommonPostStateChangeCallbackObject(item);
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