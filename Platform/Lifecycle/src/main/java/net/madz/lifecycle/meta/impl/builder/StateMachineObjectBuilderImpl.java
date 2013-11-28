package net.madz.lifecycle.meta.impl.builder;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.madz.bcel.intercept.InterceptContext;
import net.madz.bcel.intercept.UnlockableStack;
import net.madz.lifecycle.LifecycleCommonErrors;
import net.madz.lifecycle.LifecycleContext;
import net.madz.lifecycle.LifecycleException;
import net.madz.lifecycle.LifecycleLockStrategry;
import net.madz.lifecycle.StateConverter;
import net.madz.lifecycle.SyntaxErrors;
import net.madz.lifecycle.annotations.LifecycleLock;
import net.madz.lifecycle.annotations.Null;
import net.madz.lifecycle.annotations.StateIndicator;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.annotations.action.Condition;
import net.madz.lifecycle.annotations.action.ConditionalTransition;
import net.madz.lifecycle.annotations.relation.Parent;
import net.madz.lifecycle.annotations.relation.Relation;
import net.madz.lifecycle.annotations.state.Converter;
import net.madz.lifecycle.meta.builder.ConditionObjectBuilder;
import net.madz.lifecycle.meta.builder.StateMachineMetaBuilder;
import net.madz.lifecycle.meta.builder.StateMachineObjectBuilder;
import net.madz.lifecycle.meta.impl.builder.helper.CallbackMethodConfigureScanner;
import net.madz.lifecycle.meta.impl.builder.helper.CallbackMethodVerificationScanner;
import net.madz.lifecycle.meta.impl.builder.helper.ConditionProviderMethodScanner;
import net.madz.lifecycle.meta.impl.builder.helper.CoverageVerifier;
import net.madz.lifecycle.meta.impl.builder.helper.MethodSignatureScanner;
import net.madz.lifecycle.meta.impl.builder.helper.RelationGetterConfigureScanner;
import net.madz.lifecycle.meta.impl.builder.helper.RelationGetterScanner;
import net.madz.lifecycle.meta.impl.builder.helper.RelationIndicatorPropertyMethodScanner;
import net.madz.lifecycle.meta.impl.builder.helper.ScannerForVerifyConditionCoverage;
import net.madz.lifecycle.meta.impl.builder.helper.StateIndicatorDefaultMethodScanner;
import net.madz.lifecycle.meta.impl.builder.helper.StateIndicatorGetterMethodScanner;
import net.madz.lifecycle.meta.impl.builder.helper.TransitionMethodScanner;
import net.madz.lifecycle.meta.instance.ConditionObject;
import net.madz.lifecycle.meta.instance.FunctionMetadata;
import net.madz.lifecycle.meta.instance.RelationObject;
import net.madz.lifecycle.meta.instance.StateMachineObject;
import net.madz.lifecycle.meta.instance.StateObject;
import net.madz.lifecycle.meta.instance.TransitionObject;
import net.madz.lifecycle.meta.template.ConditionMetadata;
import net.madz.lifecycle.meta.template.RelationConstraintMetadata;
import net.madz.lifecycle.meta.template.RelationMetadata;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.lifecycle.meta.template.StateMetadata;
import net.madz.lifecycle.meta.template.TransitionMetadata;
import net.madz.meta.KeySet;
import net.madz.util.KeyedList;
import net.madz.util.MethodScanCallback;
import net.madz.util.MethodScanner;
import net.madz.util.StringUtil;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailureSet;

public class StateMachineObjectBuilderImpl<S> extends ObjectBuilderBase<StateMachineObject<S>, StateMachineObject<S>, StateMachineMetadata> implements
        StateMachineObjectBuilder<S> {

    private final HashMap<TransitionMetadata, LinkedList<TransitionObject>> transitionMetadataMap = new HashMap<>();
    private final KeyedList<TransitionObject> transitionObjectList = new KeyedList<>();
    private final KeyedList<ConditionObject> conditionObjectList = new KeyedList<>();
    @SuppressWarnings("rawtypes")
    private final KeyedList<StateObject> stateObjectList = new KeyedList<>();
    private final HashMap<Object, RelationObject> relationObjectsMap = new HashMap<>();
    private final ArrayList<RelationObject> relationObjectList = new ArrayList<>();
    private final ArrayList<CallbackObject> specificPreStateChangeCallbackObjects = new ArrayList<>();
    private final ArrayList<CallbackObject> specificPostStateChangeCallbackObjects = new ArrayList<>();
    private final ArrayList<CallbackObject> commonPreStateChangeCallbackObjects = new ArrayList<>();
    private final ArrayList<CallbackObject> commonPostStateChangeCallbackObjects = new ArrayList<>();
    private StateAccessor<String> stateAccessor;
    private RelationObject parentRelationObject;
    private LifecycleLockStrategry lifecycleLockStrategry;
    private StateConverter<S> stateConverter;

    public StateMachineObjectBuilderImpl(StateMachineMetaBuilder template, String name) {
        super(null, name);
        this.setMetaType(template);
    }

    @Override
    public StateConverter<S> getStateConverter() {
        return this.stateConverter;
    }

    @Override
    public boolean isLockEnabled() {
        return null != lifecycleLockStrategry;
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {
        // TODO Auto-generated method stub
    }

    private void configureLifecycleLock(Class<?> klass) throws VerificationException {
        final LifecycleLock annotation = klass.getAnnotation(LifecycleLock.class);
        if ( annotation != null ) {
            try {
                this.lifecycleLockStrategry = annotation.value().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw newVerificationException(getDottedPath(), SyntaxErrors.LIFECYCLE_LOCK_SHOULD_HAVE_NO_ARGS_CONSTRUCTOR, annotation.value());
            }
        }
    }

    private void configureRelationObject(Class<?> klass) throws VerificationException {
        configureRelationObjectsFromField(klass);
        configureRelationObjectsOnProperties(klass);
    }

    private void configureRelationObjectsOnProperties(Class<?> klass) throws VerificationException {
        if ( Object.class == klass || null == klass ) {
            return;
        }
        final VerificationFailureSet failureSet = new VerificationFailureSet();
        RelationGetterConfigureScanner scanner = new RelationGetterConfigureScanner(this, this, failureSet);
        MethodScanner.scanMethodsOnClasses(klass, scanner);
        if ( 0 < failureSet.size() ) throw new VerificationException(failureSet);
    }

    private void configureRelationObjectsFromField(Class<?> klass) throws VerificationException {
        if ( null == klass || klass.isInterface() || Object.class == klass ) {
            return;
        }
        final ArrayList<RelationMetadata> extendedRelationMetadata = new ArrayList<>();
        for ( Class<?> clazz = klass; clazz != Object.class; clazz = clazz.getSuperclass() ) {
            for ( Field field : clazz.getDeclaredFields() ) {
                if ( null == field.getAnnotation(Relation.class) ) {
                    continue;
                }
                final Object relationKey = getRelationKey(field);
                final RelationMetadata relationMetadata = getMetaType().getRelationMetadata(relationKey);
                if ( extendedRelationMetadata.contains(relationMetadata) ) {
                    continue;
                }
                if ( relationMetadata.hasSuper() ) {
                    markExtendedRelationMetadata(extendedRelationMetadata, relationMetadata);
                }
                final RelationObject relationObject = new RelationObjectBuilderImpl(this, field, relationMetadata).build(klass, this).getMetaData();
                addRelation(klass, relationObject, relationMetadata.getPrimaryKey());
            }
        }
    }

    private Object getRelationKey(Field field) {
        final Class<?> relationClass = field.getAnnotation(Relation.class).value();
        if ( Null.class != relationClass ) {
            return relationClass;
        } else {
            return StringUtil.toUppercaseFirstCharacter(field.getName());
        }
    }

    private void markExtendedRelationMetadata(final ArrayList<RelationMetadata> extendedRelationMetadata, final RelationMetadata relationMetadata) {
        if ( extendedRelationMetadata == null || relationMetadata == null ) {
            return;
        }
        extendedRelationMetadata.add(relationMetadata);
        if ( relationMetadata.hasSuper() ) {
            markExtendedRelationMetadata(extendedRelationMetadata, relationMetadata.getSuper());
        }
    }

    public void addRelation(Class<?> klass, final RelationObject relationObject, final Object primaryKey) {
        this.relationObjectsMap.put(primaryKey, relationObject);
        this.relationObjectList.add(relationObject);
        // [TODO] [Tracy] Need to test parent
        if ( isParentRelation(klass) ) {
            this.parentRelationObject = relationObject;
        }
    }

    private boolean isParentRelation(Class<?> klass) {
        return null != klass.getAnnotation(Parent.class);
    }

    private void configureStateObjects(Class<?> klass) throws VerificationException {
        final StateMetadata[] allStates = getMetaType().getAllStates();
        for ( StateMetadata stateMetadata : allStates ) {
            StateObjectBuilderImpl<S> stateObject = new StateObjectBuilderImpl<S>(this, stateMetadata);
            stateObject.setRegistry(getRegistry());
            stateObject.build(klass, this);
            this.stateObjectList.add(stateObject.getMetaData());
        }
    }

    private void configureStateIndicatorAccessor(Class<?> klass) throws VerificationException {
        if ( !klass.isInterface() ) {
            Field specifiedStateField = findFieldWith(klass, StateIndicator.class);
            if ( null != specifiedStateField ) {
                configureFieldStateAccessor(specifiedStateField);
                return;
            }
        }
        final Method specifiedGetter = findCustomizedStateIndicatorGetter(klass);
        if ( null != specifiedGetter ) {
            configurePropertyAccessor(klass, specifiedGetter);
        } else {
            final Method defaultGetter = findDefaultStateGetterMethod(klass);
            configurePropertyAccessor(klass, defaultGetter);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void configurePropertyAccessor(Class<?> klass, Method getter) {
        final String setterName = convertSetterName(getter.getName(), getter.getReturnType());
        final Setter setter;
        if ( klass.isInterface() ) {
            setter = new LazySetterImpl<>(getter);
        } else {
            final Method setterMethod = findMethod(klass, setterName, getter.getReturnType());
            setter = new EagerSetterImpl<>(setterMethod);
        }
        if ( String.class.equals(getter.getReturnType()) ) {
            this.stateAccessor = new PropertyAccessor<String>(getter, setter);
        } else {
            try {
                this.stateConverter = (StateConverter<S>) getter.getAnnotation(Converter.class).value().newInstance();
                this.stateAccessor = new ConverterAccessor(stateConverter, new PropertyAccessor(getter, setter));
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void configureFieldStateAccessor(Field stateField) {
        if ( String.class.equals(stateField.getType()) ) {
            this.stateAccessor = new FieldStateAccessor<String>(stateField);
        } else {
            try {
                this.stateConverter = (StateConverter<S>) stateField.getAnnotation(Converter.class).value().newInstance();
                this.stateAccessor = new ConverterAccessor(stateConverter, new FieldStateAccessor(stateField));
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private void verifySyntax(Class<?> klass) throws VerificationException {
        verifyTransitionMethods(klass);
        verifyStateIndicator(klass);
        verifyRelations(klass);
        verifyConditions(klass);
        verifyCallbackMethods(klass);
    }

    private void verifyCallbackMethods(Class<?> klass) throws VerificationException {
        final VerificationFailureSet failureSet = new VerificationFailureSet();
        final CallbackMethodVerificationScanner scanner = new CallbackMethodVerificationScanner(this, failureSet);
        MethodScanner.scanMethodsOnClasses(klass, scanner);
        if ( failureSet.size() > 0 ) {
            throw new VerificationException(failureSet);
        }
    }

    private void verifyConditions(Class<?> klass) throws VerificationException {
        verifyConditionReferenceValid(klass);
        verifyAllConditionBeCovered(klass);
    }

    private void verifyAllConditionBeCovered(Class<?> klass) throws VerificationException {
        for ( ConditionMetadata conditionMetadata : getMetaType().getAllCondtions() ) {
            verifyConditionBeCovered(klass, conditionMetadata);
        }
    }

    private void verifyConditionBeCovered(Class<?> klass, final ConditionMetadata conditionMetadata) throws VerificationException {
        final ScannerForVerifyConditionCoverage scanner = new ScannerForVerifyConditionCoverage(conditionMetadata);
        MethodScanner.scanMethodsOnClasses(klass, scanner);
        if ( !scanner.isCovered() ) {
            throw newVerificationException(getDottedPath(), SyntaxErrors.LM_CONDITION_NOT_COVERED, klass, getMetaType().getDottedPath(),
                    conditionMetadata.getDottedPath());
        }
    }

    private void verifyConditionReferenceValid(Class<?> klass) throws VerificationException {
        final VerificationFailureSet failureSet = new VerificationFailureSet();
        MethodScanner.scanMethodsOnClasses(klass, new ConditionProviderMethodScanner(this, klass, getMetaType(), failureSet));
        if ( failureSet.size() > 0 ) {
            throw new VerificationException(failureSet);
        }
    }

    private void verifyRelations(Class<?> klass) throws VerificationException {
        verifyRelationInstancesDefinedCorrectly(klass);
        verifyRelationsAllBeCoveraged(klass);
    }

    private void verifyRelationsAllBeCoveraged(Class<?> klass) throws VerificationException {
        StateMetadata[] allStates = getMetaType().getAllStates();
        for ( StateMetadata state : allStates ) {
            for ( RelationConstraintMetadata relation : state.getValidWhiles() ) {
                for ( TransitionMetadata transition : state.getPossibleLeavingTransitions() ) {
                    verifyRelationBeCovered(klass, relation, transition);
                }
            }
            for ( RelationConstraintMetadata relation : state.getDeclaredInboundWhiles() ) {
                for ( TransitionMetadata transition : getTransitionsToState(state) ) {
                    verifyRelationBeCovered(klass, relation, transition);
                }
            }
        }
    }

    private TransitionMetadata[] getTransitionsToState(StateMetadata state) {
        final ArrayList<TransitionMetadata> transitions = new ArrayList<TransitionMetadata>();
        for ( final StateMetadata stateMetadata : getMetaType().getAllStates() ) {
            for ( final TransitionMetadata transitionMetadata : stateMetadata.getPossibleLeavingTransitions() ) {
                if ( isTransitionIn(state, transitionMetadata) ) {
                    transitions.add(transitionMetadata);
                }
            }
        }
        return transitions.toArray(new TransitionMetadata[0]);
    }

    private boolean isTransitionIn(StateMetadata state, TransitionMetadata transitionMetadata) {
        for ( final StateMetadata stateMetadata : getMetaType().getAllStates() ) {
            for ( FunctionMetadata item : stateMetadata.getDeclaredFunctionMetadata() ) {
                if ( item.getTransition().getDottedPath().equals(transitionMetadata.getDottedPath()) ) {
                    for ( StateMetadata nextState : item.getNextStates() ) {
                        if ( nextState.getDottedPath() == state.getDottedPath() ) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void verifyRelationBeCovered(Class<?> klass, final RelationConstraintMetadata relation, final TransitionMetadata transition)
            throws VerificationException {
        final TransitionMethodScanner scanner = new TransitionMethodScanner(transition);
        MethodScanner.scanMethodsOnClasses(klass, scanner);
        final Method[] transitionMethods = scanner.getTransitionMethods();
        NEXT_TRANSITION_METHOD: for ( final Method method : transitionMethods ) {
            if ( hasRelationOnMethodParameters(relation, method) ) {
                continue NEXT_TRANSITION_METHOD;
            }
            // Continue to check in field and property method
            if ( !klass.isInterface() && scanFieldsRelation(klass, relation) ) {
                continue NEXT_TRANSITION_METHOD;
            }
            final RelationGetterScanner relationGetterScanner = new RelationGetterScanner(this, relation);
            MethodScanner.scanMethodsOnClasses(klass, relationGetterScanner);
            if ( relationGetterScanner.isCovered() ) {
                continue NEXT_TRANSITION_METHOD;
            }
            throw new VerificationException(newVerificationFailure(getDottedPath(), SyntaxErrors.LM_RELATION_NOT_BE_CONCRETED, method.getName(),
                    klass.getName(), relation.getDottedPath().getName(), relation.getParent().getDottedPath()));
        }
    }

    private boolean hasRelationOnMethodParameters(final RelationConstraintMetadata relation, final Method method) throws VerificationException {
        for ( Annotation[] annotations : method.getParameterAnnotations() ) {
            for ( Annotation annotation : annotations ) {
                if ( annotation instanceof Relation ) {
                    Relation r = (Relation) annotation;
                    if ( Null.class == r.value() ) {
                        throw newVerificationException(getDottedPath(), SyntaxErrors.LM_RELATION_ON_METHOD_PARAMETER_MUST_SPECIFY_VALUE, method);
                    }
                    if ( isKeyOfRelationMetadata(relation, r.value()) ) return true;
                }
            }
        }
        return false;
    }

    private boolean scanFieldsRelation(Class<?> klass, final RelationConstraintMetadata relation) {
        for ( Class<?> c = klass; Object.class != c; c = c.getSuperclass() ) {
            for ( Field field : c.getDeclaredFields() ) {
                if ( hasRelationOnField(relation, field) ) return true;
            }
        }
        return false;
    }

    private boolean hasRelationOnField(final RelationConstraintMetadata relation, Field field) {
        Relation r = field.getAnnotation(Relation.class);
        if ( null == r ) return false;
        if ( Null.class != r.value() ) {
            if ( isKeyOfRelationMetadata(relation, r.value()) ) {
                return true;
            }
        } else {
            if ( isKeyOfRelationMetadata(relation, StringUtil.toUppercaseFirstCharacter(field.getName())) ) {
                return true;
            }
        }
        return false;
    }

    public boolean isKeyOfRelationMetadata(final RelationConstraintMetadata relation, Object key) {
        return relation.getKeySet().contains(key);
    }

    private void verifyRelationInstancesDefinedCorrectly(Class<?> klass) throws VerificationException {
        verifyRelationInstanceNotBeyondStateMachine(klass);
        verifyRelationInstancesUnique(klass);
    }

    private void verifyRelationInstanceNotBeyondStateMachine(Class<?> klass) throws VerificationException {
        if ( null == klass ) {
            return;
        }
        verifyRelationInstanceOnFieldNotBeyondStateMachine(klass);
        verifyRelationInstanceOnMethodNotBeyondStateMachine(klass);
    }

    private void verifyRelationInstanceOnMethodNotBeyondStateMachine(Class<?> klass) throws VerificationException {
        if ( Object.class == klass || null == klass ) {
            return;
        }
        final VerificationFailureSet failureSet = new VerificationFailureSet();
        RelationIndicatorPropertyMethodScanner scanner = new RelationIndicatorPropertyMethodScanner(this, failureSet);
        MethodScanner.scanMethodsOnClasses(klass, scanner);
        if ( failureSet.size() > 0 ) {
            throw new VerificationException(failureSet);
        }
    }

    private void verifyRelationInstanceOnFieldNotBeyondStateMachine(Class<?> klass) throws VerificationException {
        if ( null == klass || klass.isInterface() || Object.class == klass ) {
            return;
        }
        for ( Field field : klass.getDeclaredFields() ) {
            Relation relation = field.getAnnotation(Relation.class);
            if ( null == relation ) {
                continue;
            }
            Class<?> relationClass = relation.value();
            if ( Null.class != relationClass ) {
                if ( !getMetaType().hasRelation(relationClass) ) {
                    throw new VerificationException(newVerificationFailure(getDottedPath(), SyntaxErrors.LM_REFERENCE_INVALID_RELATION_INSTANCE,
                            klass.getName(), relationClass.getName(), getMetaType().getDottedPath().getAbsoluteName()));
                }
            } else {
                if ( !getMetaType().hasRelation(StringUtil.toUppercaseFirstCharacter(field.getName())) ) {
                    throw new VerificationException(newVerificationFailure(getDottedPath(), SyntaxErrors.LM_REFERENCE_INVALID_RELATION_INSTANCE,
                            klass.getName(), relationClass.getName(), getMetaType().getDottedPath().getAbsoluteName()));
                }
            }
        }
        verifyRelationInstanceOnFieldNotBeyondStateMachine(klass.getSuperclass());
    }

    private void verifyRelationInstancesUnique(Class<?> klass) throws VerificationException {
        final Set<Class<?>> relations = new HashSet<>();
        for ( Field field : klass.getDeclaredFields() ) {
            final Relation relation = field.getAnnotation(Relation.class);
            checkRelationInstanceWhetherExists(klass, relations, relation);
        }
        for ( final Method method : klass.getDeclaredMethods() ) {
            if ( method.getName().startsWith("get") && method.getTypeParameters().length <= 0 ) {
                if ( method.getAnnotation(Relation.class) != null ) {
                    checkRelationInstanceWhetherExists(klass, relations, method.getAnnotation(Relation.class));
                }
            } else {
                if ( method.getAnnotation(Relation.class) != null ) {
                    final Set<Class<?>> methodRelations = new HashSet<>();
                    final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                    for ( final Annotation[] annotations : parameterAnnotations ) {
                        for ( final Annotation annotation : annotations ) {
                            if ( annotation instanceof Relation ) {
                                final Relation r = (Relation) annotation;
                                checkRelationInstanceWhetherExists(klass, methodRelations, r);
                            }
                        }
                    }
                }
            }
        }
    }

    private void checkRelationInstanceWhetherExists(Class<?> klass, final Set<Class<?>> relations, final Relation relation) throws VerificationException {
        if ( null != relation ) {
            if ( relations.contains(relation.value()) ) {
                throw newVerificationException(getDottedPath(), SyntaxErrors.LM_RELATION_INSTANCE_MUST_BE_UNIQUE, klass.getName(), relation.value().getName());
            }
            relations.add(relation.value());
        }
    }

    private void verifyStateIndicator(Class<?> klass) throws VerificationException {
        if ( !klass.isInterface() ) {
            Field specifiedStateField = findFieldWith(klass, StateIndicator.class);
            if ( null != specifiedStateField ) {
                verifyStateIndicatorElement(klass, specifiedStateField, specifiedStateField.getType());
                return;
            }
        }
        final Method specifiedGetter = findCustomizedStateIndicatorGetter(klass);
        if ( null != specifiedGetter ) {
            verifyStateIndicatorElement(klass, specifiedGetter, specifiedGetter.getReturnType());
        } else {
            // verify default
            final Method defaultGetter = findDefaultStateGetterMethod(klass);
            if ( null == defaultGetter ) {
                throw newVerificationException(getDottedPath(), SyntaxErrors.STATE_INDICATOR_CANNOT_FIND_DEFAULT_AND_SPECIFIED_STATE_INDICATOR, klass);
            } else {
                verifyStateIndicatorElement(klass, defaultGetter, defaultGetter.getReturnType());
            }
        }
    }

    private Method findCustomizedStateIndicatorGetter(Class<?> klass) throws VerificationException {
        final VerificationFailureSet failureSet = new VerificationFailureSet();
        final StateIndicatorGetterMethodScanner scanner = new StateIndicatorGetterMethodScanner(this, klass, failureSet);
        MethodScanner.scanMethodsOnClasses(klass, scanner);
        if ( failureSet.size() > 0 )
            throw new VerificationException(failureSet);
        else {
            final Method specifiedGetter = scanner.getStateGetterMethod();
            return specifiedGetter;
        }
    }

    private void verifyStateIndicatorElement(Class<?> klass, AnnotatedElement getter, Class<?> stateType) throws VerificationException {
        verifyStateIndicatorElementSetterVisibility(klass, getter, stateType);
        if ( stateType.equals(java.lang.String.class) ) {
            return;
        }
        verifyStateIndicatorConverter(getter, stateType);
    }

    private void verifyStateIndicatorConverter(AnnotatedElement getter, Class<?> stateType) throws VerificationException {
        final Class<?> getterDeclaringClass;
        if ( getter instanceof Method ) {
            getterDeclaringClass = ( (Method) getter ).getDeclaringClass();
        } else if ( getter instanceof Field ) {
            getterDeclaringClass = ( (Field) getter ).getDeclaringClass();
        } else {
            throw new IllegalArgumentException();
        }
        final Converter converterMeta = getter.getAnnotation(Converter.class);
        if ( null == converterMeta ) {
            throw newVerificationException(getDottedPath(), SyntaxErrors.STATE_INDICATOR_CONVERTER_NOT_FOUND, getterDeclaringClass, stateType);
        } else {
            Type[] genericInterfaces = converterMeta.value().getGenericInterfaces();
            for ( Type type : genericInterfaces ) {
                if ( type instanceof ParameterizedType ) {
                    ParameterizedType pType = (ParameterizedType) type;
                    if ( pType.getRawType() instanceof Class && StateConverter.class.isAssignableFrom((Class<?>) pType.getRawType()) ) {
                        if ( !stateType.equals(pType.getActualTypeArguments()[0]) ) {
                            throw newVerificationException(getDottedPath(), SyntaxErrors.STATE_INDICATOR_CONVERTER_INVALID, getterDeclaringClass, stateType,
                                    converterMeta.value(), pType.getActualTypeArguments()[0]);
                        }
                    }
                } else {
                    continue;
                }
            }
        }
    }

    private void verifyStateIndicatorElementSetterVisibility(final Class<?> klass, AnnotatedElement getter, Class<?> returnType) throws VerificationException {
        if ( getter instanceof Method ) {
            final String getterName = ( (Method) getter ).getName();
            final String setterName = convertSetterName(getterName, returnType);
            final Method setter = findMethod(klass, setterName, returnType);
            if ( null == setter && !klass.isInterface() ) {
                throw newVerificationException(getDottedPath(), SyntaxErrors.STATE_INDICATOR_SETTER_NOT_FOUND, ( (Method) getter ).getDeclaringClass());
            } else {
                if ( null != setter && !Modifier.isPrivate(( setter ).getModifiers()) ) {
                    throw newVerificationException(getDottedPath(), SyntaxErrors.STATE_INDICATOR_CANNOT_EXPOSE_STATE_INDICATOR_SETTER, setter);
                }
            }
        } else if ( getter instanceof Field ) {
            if ( !Modifier.isPrivate(( (Field) getter ).getModifiers()) ) {
                throw newVerificationException(getDottedPath(), SyntaxErrors.STATE_INDICATOR_CANNOT_EXPOSE_STATE_INDICATOR_FIELD, getter);
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    private Method findMethod(Class<?> klass, String setterName, Class<?> returnType) {
        final MethodSignatureScanner scanner = new MethodSignatureScanner(setterName, new Class<?>[] { returnType });
        MethodScanner.scanMethodsOnClasses(klass, scanner);
        return scanner.getMethod();
    }

    private String convertSetterName(String getterName, Class<?> type) {
        if ( type != Boolean.TYPE && type != Boolean.class ) {
            return "set" + getterName.substring(3);
        } else {
            return "set" + getterName.substring(2);
        }
    }

    private Method findDefaultStateGetterMethod(Class<?> klass) {
        final StateIndicatorDefaultMethodScanner scanner = new StateIndicatorDefaultMethodScanner();
        MethodScanner.scanMethodsOnClasses(klass, scanner);
        return scanner.getDefaultMethod();
    }

    private Field findFieldWith(Class<?> klass, Class<? extends Annotation> aClass) {
        for ( Class<?> index = klass; index != Object.class; index = index.getSuperclass() ) {
            for ( Field field : index.getDeclaredFields() ) {
                if ( null != field.getAnnotation(aClass) ) {
                    return field;
                }
            }
        }
        return null;
    }

    private void verifyTransitionMethods(Class<?> klass) throws VerificationException {
        final VerificationFailureSet failureSet = new VerificationFailureSet();
        verifyTransitionMethodsValidity(klass, failureSet);
        verifyAllTransitionsCoverage(klass, failureSet);
        if ( failureSet.size() > 0 ) {
            throw new VerificationException(failureSet);
        }
    }

    private void verifyAllTransitionsCoverage(Class<?> klass, VerificationFailureSet failureSet) {
        for ( TransitionMetadata transitionMetadata : getMetaType().getAllTransitions() ) {
            verifyTransitionBeCovered(klass, transitionMetadata, failureSet);
        }
    }

    private void verifyTransitionBeCovered(Class<?> klass, final TransitionMetadata transitionMetadata, final VerificationFailureSet failureSet) {
        CoverageVerifier coverage = new CoverageVerifier(this, transitionMetadata, failureSet);
        MethodScanner.scanMethodsOnClasses(klass, coverage);
        if ( coverage.notCovered() ) {
            failureSet.add(newVerificationFailure(transitionMetadata.getDottedPath().getAbsoluteName(), SyntaxErrors.LM_TRANSITION_NOT_CONCRETED_IN_LM,
                    transitionMetadata.getDottedPath().getName(), getMetaType().getDottedPath().getAbsoluteName(), klass.getSimpleName()));
        }
    }

    private void verifyTransitionMethodsValidity(final Class<?> klass, final VerificationFailureSet failureSet) {
        MethodScanner.scanMethodsOnClasses(klass, new MethodScanCallback() {

            @Override
            public boolean onMethodFound(Method method) {
                verifyTransitionMethod(method, failureSet);
                return false;
            }
        });
    }

    private void configureConditions(final Class<?> klass) {
        MethodScanner.scanMethodsOnClasses(klass, new MethodScanCallback() {

            @Override
            public boolean onMethodFound(Method method) {
                final Condition conditionMeta = method.getAnnotation(Condition.class);
                if ( null == conditionMeta ) {
                    return false;
                }
                final ConditionMetadata conditionMetadata = getMetaType().getCondtion(conditionMeta.value());
                try {
                    configureCondition(klass, method, conditionMetadata);
                } catch (VerificationException e) {
                    throw new IllegalStateException(e);
                }
                return false;
            }
        });
    }

    protected void configureCondition(Class<?> klass, Method method, ConditionMetadata conditionMetadata) throws VerificationException {
        ConditionObjectBuilder builder = new ConditionObjectBuilderImpl(this, method, conditionMetadata);
        builder.build(klass, this);
        conditionObjectList.add(builder.getMetaData());
    }

    private void configureTransitionObjects(final Class<?> klass) {
        MethodScanner.scanMethodsOnClasses(klass, new MethodScanCallback() {

            @Override
            public boolean onMethodFound(Method method) {
                final Transition transitionAnno = method.getAnnotation(Transition.class);
                if ( null == transitionAnno ) {
                    return false;
                }
                final TransitionMetadata transitionMetadata;
                if ( Null.class == transitionAnno.value() ) {
                    transitionMetadata = getMetaType().getTransition(StringUtil.toUppercaseFirstCharacter(method.getName()));
                } else {
                    transitionMetadata = getMetaType().getTransition(transitionAnno.value());
                }
                try {
                    configureTransitionObject(klass, method, transitionMetadata);
                } catch (VerificationException e) {
                    throw new IllegalStateException(e);
                }
                return false;
            }
        });
    }

    private void verifyTransitionMethod(Method method, VerificationFailureSet failureSet) {
        final Transition transition = method.getAnnotation(Transition.class);
        if ( transition == null ) {
            return;
        }
        TransitionMetadata transitionMetadata = null;
        if ( Null.class == transition.value() ) {
            transitionMetadata = verifyTransitionMethodDefaultStyle(method, failureSet, transitionMetadata);
        } else {
            transitionMetadata = verifyTransitionMethodWithTransitionClassKey(method, failureSet, transition, transitionMetadata);
        }
        if ( null != transitionMetadata ) {
            verifySpecialTransitionMethodHasZeroArgument(method, failureSet, transitionMetadata);
        }
    }

    private void configureTransitionObject(final Class<?> klass, final Method method, final TransitionMetadata transitionMetadata) throws VerificationException {
        final TransitionObjectBuilderImpl transitionObjectBuilder = new TransitionObjectBuilderImpl(this, method, transitionMetadata);
        transitionObjectBuilder.build(klass, this);
        final TransitionObject transitionObject = transitionObjectBuilder.getMetaData();
        transitionObjectList.add(transitionObject);
        if ( null == transitionMetadataMap.get(transitionMetadata) ) {
            final LinkedList<TransitionObject> transitionObjects = new LinkedList<>();
            transitionObjects.add(transitionObject);
            transitionMetadataMap.put(transitionMetadata, transitionObjects);
        } else {
            transitionMetadataMap.get(transitionMetadata).add(transitionObject);
        }
    }

    private TransitionMetadata verifyTransitionMethodWithTransitionClassKey(Method method, VerificationFailureSet failureSet, final Transition transition,
            TransitionMetadata transitionMetadata) {
        if ( !getMetaType().hasTransition(transition.value()) ) {
            failureSet.add(newVerificationFailure(getMethodDottedPath(method), SyntaxErrors.LM_TRANSITION_METHOD_WITH_INVALID_TRANSITION_REFERENCE, transition,
                    method.getName(), method.getDeclaringClass().getName(), getMetaType().getDottedPath()));
        } else {
            transitionMetadata = getMetaType().getTransition(transition.value());
        }
        return transitionMetadata;
    }

    private TransitionMetadata verifyTransitionMethodDefaultStyle(Method method, VerificationFailureSet failureSet, TransitionMetadata transitionMetadata) {
        if ( !getMetaType().hasTransition(StringUtil.toUppercaseFirstCharacter(method.getName())) ) {
            failureSet.add(newVerificationFailure(getMethodDottedPath(method), SyntaxErrors.LM_METHOD_NAME_INVALID, getMetaType().getDottedPath(),
                    method.getName(), method.getDeclaringClass().getName()));
        } else {
            transitionMetadata = getMetaType().getTransition(StringUtil.toUppercaseFirstCharacter(method.getName()));
        }
        return transitionMetadata;
    }

    private void verifySpecialTransitionMethodHasZeroArgument(Method method, VerificationFailureSet failureSet, TransitionMetadata transitionMetadata) {
        switch (transitionMetadata.getType()) {
            case Corrupt:
                // fall through
            case Recover:
                // fall through
            case Redo:
                if ( method.getParameterTypes().length > 0 ) {
                    failureSet.add(newVerificationFailure(transitionMetadata.getDottedPath(),
                            SyntaxErrors.TRANSITION_TYPE_CORRUPT_RECOVER_REDO_REQUIRES_ZERO_PARAMETER, method,
                            StringUtil.toUppercaseFirstCharacter(method.getName()), transitionMetadata.getType()));
                }
                break;
            default:
                break;
        }
    }

    private String getMethodDottedPath(Method method) {
        return method.getDeclaringClass().getName() + "." + method.getName();
    }

    @Override
    public TransitionObject[] getTransitionSet() {
        return transitionObjectList.toArray(TransitionObject.class);
    }

    @Override
    public boolean hasTransition(Object transitionKey) {
        return this.transitionObjectList.containsKey(transitionKey);
    }

    @Override
    public TransitionObject getTransition(Object transitionKey) {
        return this.transitionObjectList.get(transitionKey);
    }

    @SuppressWarnings("unchecked")
    @Override
    public StateObject<S>[] getStateSet() {
        return (StateObject<S>[]) this.stateObjectList.toArray(StateObject.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public StateObject<S> getState(Object stateKey) {
        return (StateObject<S>) stateObjectList.get(stateKey);
    }

    @Override
    public StateMachineObjectBuilder<S> build(Class<?> klass, StateMachineObject<S> parent) throws VerificationException {
        super.build(klass, parent);
        addKey(klass);
        verifySyntax(klass);
        configureStateIndicatorAccessor(klass);
        configureConditions(klass);
        configureTransitionObjects(klass);
        configureStateObjects(klass);
        configureRelationObject(klass);
        configureLifecycleLock(klass);
        configureCallbacks(klass);
        return this;
    }

    private void configureCallbacks(Class<?> klass) throws VerificationException {
        final CallbackMethodConfigureScanner scanner = new CallbackMethodConfigureScanner(this, klass);
        scanner.scanMethod();
    }

    @Override
    public StateAccessor<String> getStateAccessor() {
        return stateAccessor;
    }

    public void setStateAccessor(StateAccessor<String> accessor) {
        this.stateAccessor = accessor;
    }

    @Override
    public String evaluateState(Object target) {
        return this.stateAccessor.read(target);
    }

    @Override
    public void setTargetState(Object target, String state) {
        this.stateAccessor.write(target, state);
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getNextState(Object target, Object transitionKey) {
        final String stateName = evaluateState(target);
        final StateObject<S> state = getState(stateName);
        final FunctionMetadata functionMetadata = state.getMetaType().getFunctionMetadata(transitionKey);
        if ( null == functionMetadata ) {
            throw new IllegalArgumentException("Invalid Key or Key not registered: " + transitionKey + " while searching function metadata from state: "
                    + state);
        }
        if ( 1 < functionMetadata.getNextStates().size() ) {
            final TransitionMetadata transitionMetadata = functionMetadata.getTransition();
            Class<? extends ConditionalTransition<?>> judgerClass = transitionMetadata.getJudgerClass();
            try {
                ConditionalTransition<Object> conditionalTransition = (ConditionalTransition<Object>) judgerClass.newInstance();
                final Class<?> nextStateClass = conditionalTransition.doConditionJudge(evaluateJudgeable(target, transitionMetadata));
                final StateMetadata nextState = handleCompositeStateMachineLinkage(getState(nextStateClass).getMetaType());
                return nextState.getSimpleName();
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new IllegalStateException("Cannot create judger instance of Class: " + judgerClass + ". Please provide no-arg constructor.");
            }
        } else if ( 1 == functionMetadata.getNextStates().size() ) {
            StateMetadata nextState = findStateFromBottomToTop(functionMetadata);
            nextState = handleCompositeStateMachineLinkage(nextState);
            return nextState.getSimpleName();
        } else {
            throw new IllegalArgumentException("No next states found with fuction: " + functionMetadata);
        }
    }

    private StateMetadata findStateFromBottomToTop(final FunctionMetadata functionMetadata) {
        StateMetadata nextState = functionMetadata.getNextStates().get(0);
        nextState = getState(nextState.getPrimaryKey()).getMetaType();
        return nextState;
    }

    private Object evaluateJudgeable(Object target, final TransitionMetadata transitionMetadata) throws IllegalAccessException, InvocationTargetException {
        final ConditionObject conditionObject = getConditionObject(transitionMetadata.getConditionClass());
        Object getJudgeable = conditionObject.conditionGetter().invoke(target);
        return getJudgeable;
    }

    private StateMetadata handleCompositeStateMachineLinkage(StateMetadata nextState) {
        if ( nextState.isCompositeState() ) {
            nextState = nextState.getCompositeStateMachine().getInitialState();
        } else if ( nextState.getStateMachine().isComposite() && nextState.isFinal() ) {
            nextState = nextState.getLinkTo();
        }
        if ( nextState.isCompositeState() || nextState.getStateMachine().isComposite() && nextState.isFinal() ) {
            nextState = handleCompositeStateMachineLinkage(nextState);
        }
        return nextState;
    }

    private ConditionObject getConditionObject(Class<?> conditionClass) {
        return this.conditionObjectList.get(conditionClass);
    }

    @Override
    public void validateValidWhiles(final InterceptContext<?, ?> context) {
        // final HashMap<Class<?>, Object> relationsInMethodParameters =
        // evaluatorRelationsInMethodParameters(context);
        final Object target = context.getTarget();
        validateValidWhiles(target, context);
    }

    @Override
    public void validateValidWhiles(final Object target, final UnlockableStack stack) {
        final StateMetadata state = getMetaType().getState(evaluateState(target));
        final RelationConstraintMetadata[] validWhiles = state.getValidWhiles();
        final HashMap<String, List<RelationConstraintMetadata>> mergedRelations = mergeRelations(validWhiles);
        final StateObject<S> stateObject = getState(state.getDottedPath());
        for ( final Entry<String, List<RelationConstraintMetadata>> relationMetadataEntry : mergedRelations.entrySet() ) {
            final Object relationInstance = getRelationInstance(target, new HashMap<Class<?>, Object>(), relationMetadataEntry);
            if ( null == relationInstance ) {
                for ( final RelationConstraintMetadata item : relationMetadataEntry.getValue() ) {
                    if ( !item.isNullable() ) {
                        throw new LifecycleException(getClass(), LifecycleCommonErrors.BUNDLE, LifecycleCommonErrors.VALID_WHILE_RELATION_TARGET_IS_NULL,
                                item.getPrimaryKey(), "nullable = " + item.isNullable(), state.getPrimaryKey());
                    }
                }
                continue;
            }
            stateObject.verifyValidWhile(target, relationMetadataEntry.getValue().toArray(new RelationConstraintMetadata[0]), relationInstance, stack);
        }
    }

    private Object getRelationInMethodParameters(HashMap<Class<?>, Object> relationsInMethodParameters, KeySet keySet) {
        Iterator<Object> iterator = keySet.iterator();
        while ( iterator.hasNext() ) {
            final Object key = iterator.next();
            if ( relationsInMethodParameters.containsKey(key) ) {
                return relationsInMethodParameters.get(key);
            }
        }
        return null;
    }

    private HashMap<Class<?>, Object> evaluatorRelationsInMethodParameters(InterceptContext<?, ?> context) {
        final Object[] arguments = context.getArguments();
        final Method method = context.getMethod();
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        final HashMap<Class<?>, Object> relationObjectMap = new HashMap<>();
        int parameterIndex = 0;
        for ( Annotation[] annotations : parameterAnnotations ) {
            for ( Annotation annotation : annotations ) {
                if ( Relation.class == annotation.annotationType() ) {
                    relationObjectMap.put(( (Relation) annotation ).value(), arguments[parameterIndex]);
                }
            }
            parameterIndex++;
        }
        return relationObjectMap;
    }

    private ReadAccessor<?> getEvaluator(Object relationKey) {
        if ( relationObjectsMap.containsKey(relationKey) ) {
            return (ReadAccessor<?>) relationObjectsMap.get(relationKey).getEvaluator();
        }
        throw new IllegalStateException("The evaluate is not found, which should not happen. Check the verifyRelationsAllBeCoveraged method with key:"
                + relationKey);
    }

    @Override
    public void validateInboundWhiles(InterceptContext<?, ?> context) {
        final HashMap<Class<?>, Object> relationsInMethodParameters = evaluatorRelationsInMethodParameters(context);
        final Object target = context.getTarget();
        final Object transitionKey = context.getTransitionKey();
        final StateMetadata state = getMetaType().getState(evaluateState(target));
        final String nextState = getNextState(target, transitionKey);
        final StateMetadata nextStateMetadata = getMetaType().getState(nextState);
        for ( final Entry<String, List<RelationConstraintMetadata>> relationMetadataEntry : mergeRelations(nextStateMetadata.getInboundWhiles()).entrySet() ) {
            final Object relationTarget = getRelationInstance(target, relationsInMethodParameters, relationMetadataEntry);
            if ( null == relationTarget ) {
                for ( final RelationConstraintMetadata item : relationMetadataEntry.getValue() ) {
                    if ( !item.isNullable() ) {
                        throw new LifecycleException(getClass(), LifecycleCommonErrors.BUNDLE, LifecycleCommonErrors.INBOUND_WHILE_RELATION_TARGET_IS_NULL,
                                item.getPrimaryKey(), "nullable = " + item.isNullable(), state.getPrimaryKey());
                    }
                }
                continue;
            }
            getState(state.getDottedPath()).verifyInboundWhile(transitionKey, target, nextState,
                    relationMetadataEntry.getValue().toArray(new RelationConstraintMetadata[0]), relationTarget, context);
        }
        context.setToState(nextState);
    }

    private Object getRelationInstance(Object contextTarget, final HashMap<Class<?>, Object> relationsInMethodParameters,
            final Entry<String, List<RelationConstraintMetadata>> relationMetadataEntry) {
        Object relationObject = getRelationInMethodParameters(relationsInMethodParameters, relationMetadataEntry.getValue().get(0).getKeySet());
        if ( null == relationObject ) {
            ReadAccessor<?> evaluator = getEvaluator(relationMetadataEntry.getValue().get(0).getPrimaryKey());
            relationObject = evaluator.read(contextTarget);
        }
        return relationObject;
    }

    private HashMap<String, List<RelationConstraintMetadata>> mergeRelations(RelationConstraintMetadata[] relations) {
        final HashMap<String, List<RelationConstraintMetadata>> mergedRelations = new HashMap<>();
        for ( final RelationConstraintMetadata relationMetadata : relations ) {
            final String relationKey = relationMetadata.getRelatedStateMachine().getDottedPath().getAbsoluteName();
            if ( mergedRelations.containsKey(relationKey) ) {
                final List<RelationConstraintMetadata> list = mergedRelations.get(relationKey);
                list.add(relationMetadata);
            } else {
                final ArrayList<RelationConstraintMetadata> list = new ArrayList<>();
                list.add(relationMetadata);
                mergedRelations.put(relationKey, list);
            }
        }
        return mergedRelations;
    }

    @Override
    public boolean evaluateConditionBeforeTransition(Object transitionKey) {
        TransitionMetadata transition = getMetaType().getTransition(transitionKey);
        return !transition.postValidate();
    }

    @Override
    public LifecycleLockStrategry getLifecycleLockStrategy() {
        return this.lifecycleLockStrategry;
    }

    @Override
    public Object evaluateParent(Object target) {
        return null;
    }

    @Override
    public RelationObject[] evaluateRelatives(Object target) {
        return relationObjectList.toArray(new RelationObject[0]);
    }

    @SuppressWarnings("unchecked")
    @Override
    public StateMachineObject<S> getParentStateMachine(Object target) {
        if ( null == parentRelationObject ) {
            return null;
        } else {
            final Object parentObject = parentRelationObject.getEvaluator().read(target);
            if ( null != parentObject ) {
                try {
                    return (StateMachineObject<S>) registry.loadStateMachineObject(parentObject.getClass());
                } catch (VerificationException e) {
                    throw new IllegalStateException(e);
                }
            } else {
                return null;
            }
        }
    }

    @Override
    public StateMachineObject<?> getRelatedStateMachine(Object target, Object relativeKey) {
        final ReadAccessor<?> relationEvaluator = (ReadAccessor<?>) relationObjectsMap.get(relativeKey).getEvaluator();
        if ( null == relationEvaluator ) {
            throw new IllegalArgumentException("Cannot find relation with Key: " + relativeKey);
        } else {
            Object relation = relationEvaluator.read(target);
            if ( null != relation ) {
                try {
                    return registry.loadStateMachineObject(relation.getClass());
                } catch (VerificationException e) {
                    throw new IllegalStateException(e);
                }
            } else {
                return null;
            }
        }
    }

    @Override
    public void performPreStateChangeCallback(LifecycleContext<?, S> callbackContext) {
        final String fromState = evaluateFromState(callbackContext);
        final String toState = evaluateToState(callbackContext);
        if ( null != toState ) {
            invokeSpecificPreStateChangeCallbacks(callbackContext);
        }
        final StateObject<S> fromStateObject = getState(fromState);
        fromStateObject.invokeFromPreStateChangeCallbacks(callbackContext);
        if ( null != toState ) {
            final StateObject<S> toStateObject = getState(toState);
            toStateObject.invokeToPreStateChangeCallbacks(callbackContext);
        }
        invokeCommonPreStateChangeCallbacks(callbackContext);
    }

    @Override
    public void performPostStateChangeCallback(LifecycleContext<?, S> callbackContext) {
        final String fromState = evaluateFromState(callbackContext);
        final String toState = evaluateToState(callbackContext);
        invokeSpecificPostStateChangeCallbacks(callbackContext);
        final StateObject<S> fromStateObject = getState(fromState);
        fromStateObject.invokeFromPostStateChangeCallbacks(callbackContext);
        final StateObject<S> toStateObject = getState(toState);
        toStateObject.invokeToPostStateChangeCallbacks(callbackContext);
        invokeCommonPostStateChangeCallbacks(callbackContext);
    }

    private void invokeCommonPreStateChangeCallbacks(LifecycleContext<?, S> callbackContext) {
        for ( CallbackObject callbackObject : this.commonPreStateChangeCallbackObjects ) {
            callbackObject.doCallback(callbackContext);
        }
    }

    private void invokeSpecificPreStateChangeCallbacks(LifecycleContext<?, S> callbackContext) {
        for ( CallbackObject callbackObject : this.specificPreStateChangeCallbackObjects ) {
            if ( callbackObject.matches(callbackContext) ) {
                callbackObject.doCallback(callbackContext);
            }
        }
    }

    private void invokeCommonPostStateChangeCallbacks(LifecycleContext<?, S> callbackContext) {
        for ( CallbackObject callbackObject : this.commonPostStateChangeCallbackObjects ) {
            callbackObject.doCallback(callbackContext);
        }
    }

    private void invokeSpecificPostStateChangeCallbacks(LifecycleContext<?, S> callbackContext) {
        for ( CallbackObject callbackObject : this.specificPostStateChangeCallbackObjects ) {
            if ( callbackObject.matches(callbackContext) ) {
                callbackObject.doCallback(callbackContext);
            }
        }
    }

    private String evaluateToState(LifecycleContext<?, S> callbackContext) {
        if ( null == callbackContext.getToState() ) {
            return null;
        }
        final String toState;
        if ( null != this.stateConverter ) {
            toState = stateConverter.toState(callbackContext.getToState());
        } else {
            toState = String.valueOf(callbackContext.getToState());
        }
        return toState;
    }

    private String evaluateFromState(LifecycleContext<?, S> callbackContext) {
        final String fromState;
        if ( null != this.stateConverter ) {
            fromState = stateConverter.toState(callbackContext.getFromState());
        } else {
            fromState = String.valueOf(callbackContext.getFromState());
        }
        return fromState;
    }

    @Override
    public RelationObject getRelationObject(Object primaryKey) {
        return this.relationObjectsMap.get(primaryKey);
    }

    @Override
    public RelationObject getParentRelationObject() {
        return this.parentRelationObject;
    }

    @Override
    public void addSpecificPreStateChangeCallbackObject(CallbackObject item) {
        this.specificPreStateChangeCallbackObjects.add(item);
    }

    @Override
    public void addCommonPreStateChangeCallbackObject(CallbackObject item) {
        this.commonPreStateChangeCallbackObjects.add(item);
    }

    @Override
    public void addSpecificPostStateChangeCallbackObject(CallbackObject item) {
        this.specificPostStateChangeCallbackObjects.add(item);
    }

    @Override
    public void addCommonPostStateChangeCallbackObject(CallbackObject item) {
        this.commonPostStateChangeCallbackObjects.add(item);
    }
}
