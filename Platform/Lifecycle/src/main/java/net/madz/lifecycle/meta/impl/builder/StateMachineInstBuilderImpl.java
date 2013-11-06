package net.madz.lifecycle.meta.impl.builder;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import net.madz.lifecycle.Errors;
import net.madz.lifecycle.StateConverter;
import net.madz.lifecycle.annotations.Null;
import net.madz.lifecycle.annotations.StateIndicator;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.annotations.action.Condition;
import net.madz.lifecycle.annotations.relation.Relation;
import net.madz.lifecycle.annotations.state.Converter;
import net.madz.lifecycle.annotations.state.Overrides;
import net.madz.lifecycle.meta.builder.StateMachineInstBuilder;
import net.madz.lifecycle.meta.builder.StateMachineMetaBuilder;
import net.madz.lifecycle.meta.instance.FunctionMetadata;
import net.madz.lifecycle.meta.instance.StateInst;
import net.madz.lifecycle.meta.instance.TransitionInst;
import net.madz.lifecycle.meta.template.ConditionMetadata;
import net.madz.lifecycle.meta.template.RelationMetadata;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.lifecycle.meta.template.StateMetadata;
import net.madz.lifecycle.meta.template.TransitionMetadata;
import net.madz.lifecycle.meta.template.TransitionMetadata.TransitionTypeEnum;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailureSet;

public class StateMachineInstBuilderImpl extends
        AnnotationMetaBuilderBase<StateMachineInstBuilder, StateMachineMetaBuilder> implements StateMachineInstBuilder {

    private StateMachineMetaBuilder template;
    private HashMap<Object, TransitionInst> transitionInsts = new HashMap<>();
    private HashMap<TransitionMetadata, LinkedList<TransitionInst>> transitionMetadataInstsMap = new HashMap<>();

    public StateMachineInstBuilderImpl(StateMachineMetaBuilder template, String name) {
        super(null, name);
        this.template = template;
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {
        // TODO Auto-generated method stub
    }

    public StateMachineInstBuilder build(Class<?> klass) throws VerificationException {
        verifySyntax(klass);
        return this;
    }

    private void verifySyntax(Class<?> klass) throws VerificationException {
        verifyTransitionMethods(klass);
        verifyStateIndicator(klass);
        verifyRelations(klass);
        verifyConditions(klass);
    }

    private void verifyConditions(Class<?> klass) throws VerificationException {
        verifyConditionReferenceValid(klass);
        verifyAllConditionBeCovered(klass);
    }

    private void verifyAllConditionBeCovered(Class<?> klass) throws VerificationException {
        for ( ConditionMetadata conditionMetadata : getTemplate().getAllCondtions() ) {
            verifyConditionBeCovered(klass, conditionMetadata);
        }
    }

    private void verifyConditionBeCovered(Class<?> klass, final ConditionMetadata conditionMetadata)
            throws VerificationException {
        final ScannerForVerifyConditionCoverage scanner = new ScannerForVerifyConditionCoverage(conditionMetadata);
        scanMethodsOnClasses(new Class[] { klass }, null, scanner);
        if ( !scanner.isCovered() ) {
            throw newVerificationException(getDottedPath(), Errors.LM_CONDITION_NOT_COVERED, klass,
                    getTemplate().getDottedPath(), conditionMetadata.getDottedPath());
        }
    }

    private final class ScannerForVerifyConditionCoverage implements MethodScanner {

        private final ConditionMetadata conditionMetadata;
        private boolean covered = false;

        public ScannerForVerifyConditionCoverage(ConditionMetadata conditionMetadata) {
            this.conditionMetadata = conditionMetadata;
        }

        @Override
        public boolean onMethodFound(Method method, VerificationFailureSet failureSet) {
            final Condition condition = method.getAnnotation(Condition.class);
            if ( null != condition ) {
                if ( conditionMetadata.getKeySet().contains(condition.value()) ) {
                    covered = true;
                    return true;
                }
            }
            return false;
        }

        public boolean isCovered() {
            return covered;
        }
    }

    private void verifyConditionReferenceValid(Class<?> klass) throws VerificationException {
        final VerificationFailureSet failureSet = new VerificationFailureSet();
        scanMethodsOnClasses(new Class[] { klass }, failureSet, new ConditionProviderMethodScanner(klass,
                getTemplate()));
        if ( failureSet.size() > 0 ) {
            throw new VerificationException(failureSet);
        }
    }

    private final class ConditionProviderMethodScanner implements MethodScanner {

        private HashSet<Class<?>> conditions = new HashSet<>();
        private StateMachineMetadata template;
        private Class<?> klass;

        public ConditionProviderMethodScanner(Class<?> klass, StateMachineMetadata template) {
            this.template = template;
            this.klass = klass;
        }

        @Override
        public boolean onMethodFound(Method method, VerificationFailureSet failureSet) {
            final Condition condition = method.getAnnotation(Condition.class);
            if ( null != condition ) {
                if ( template.hasCondition(condition.value()) ) {
                    if ( conditions.contains(condition.value()) ) {
                        failureSet
                                .add(newVerificationException(klass.getName(),
                                        Errors.LM_CONDITION_MULTIPLE_METHODS_REFERENCE_SAME_CONDITION, klass,
                                        condition.value()));
                    } else {
                        conditions.add(condition.value());
                    }
                } else {
                    failureSet.add(newVerificationException(klass.getName(), Errors.LM_CONDITION_REFERENCE_INVALID,
                            method, condition.value()));
                }
            }
            return false;
        }
    }

    private void verifyRelations(Class<?> klass) throws VerificationException {
        verifyRelationInstancesDefinedCorrectly(klass);
        verifyRelationsAllBeCoveraged(klass);
    }

    private void verifyRelationsAllBeCoveraged(Class<?> klass) throws VerificationException {
        StateMetadata[] allStates = getTemplate().getAllStates();
        for ( StateMetadata state : allStates ) {
            for ( RelationMetadata relation : state.getValidWhiles() ) {
                for ( TransitionMetadata transition : state.getPossibleTransitions() ) {
                    verifyRelationBeCovered(klass, relation, transition);
                }
            }
            for ( RelationMetadata relation : state.getInboundWhiles() ) {
                for ( TransitionMetadata transition : getTransitionsToState(state) ) {
                    verifyRelationBeCovered(klass, relation, transition);
                }
            }
        }
    }

    private TransitionMetadata[] getTransitionsToState(StateMetadata state) {
        final ArrayList<TransitionMetadata> transitions = new ArrayList<TransitionMetadata>();
        for ( final StateMetadata stateMetadata : getTemplate().getAllStates() ) {
            for ( final TransitionMetadata transitionMetadata : stateMetadata.getPossibleTransitions() ) {
                if ( isTransitionIn(state, transitionMetadata) ) {
                    transitions.add(transitionMetadata);
                }
            }
        }
        return transitions.toArray(new TransitionMetadata[0]);
    }

    private boolean isTransitionIn(StateMetadata state, TransitionMetadata transitionMetadata) {
        for ( final StateMetadata stateMetadata : getTemplate().getAllStates() ) {
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

    private void verifyRelationBeCovered(Class<?> klass, final RelationMetadata relation,
            final TransitionMetadata transition) throws VerificationException {
        final TransitionMethodScanner scanner = new TransitionMethodScanner(transition);
        scanMethodsOnClasses(new Class[] { klass }, null, scanner);
        final Method[] transitionMethods = scanner.getTransitionMethods();
        NEXT_TRANSITION_METHOD: for ( final Method method : transitionMethods ) {
            if ( hasRelationOnMethodParameters(relation, method) ) continue NEXT_TRANSITION_METHOD;
            // Continue to check in field and property method
            if ( !klass.isInterface() && scanFieldsRelation(klass, relation) ) continue NEXT_TRANSITION_METHOD;
            final RelationGetterScanner relationGetterScanner = new RelationGetterScanner(relation);
            scanMethodsOnClasses(new Class[] { klass }, null, relationGetterScanner);
            if ( relationGetterScanner.isCovered() ) continue NEXT_TRANSITION_METHOD;
            throw new VerificationException(newVerificationFailure(getDottedPath(),
                    Errors.LM_RELATION_NOT_BE_CONCRETED, method.getName(), klass.getName(), relation.getDottedPath()
                            .getName(), relation.getParent().getDottedPath()));
        }
    }

    private boolean hasRelationOnMethodParameters(final RelationMetadata relation, final Method method)
            throws VerificationException {
        for ( Annotation[] annotations : method.getParameterAnnotations() ) {
            for ( Annotation annotation : annotations ) {
                if ( annotation instanceof Relation ) {
                    Relation r = (Relation) annotation;
                    if ( Null.class == r.value() ) {
                        throw newVerificationException(getDottedPath(),
                                Errors.LM_RELATION_ON_METHOD_PARAMETER_MUST_SPECIFY_VALUE, method);
                    }
                    if ( isKeyOfRelationMetadata(relation, r.value()) ) return true;
                }
            }
        }
        return false;
    }

    private boolean scanFieldsRelation(Class<?> klass, final RelationMetadata relation) {
        for ( Class c = klass; Object.class != c; c = c.getSuperclass() ) {
            for ( Field field : c.getDeclaredFields() ) {
                if ( hasRelationOnField(relation, field) ) return true;
            }
        }
        return false;
    }

    private boolean hasRelationOnField(final RelationMetadata relation, Field field) {
        Relation r = field.getAnnotation(Relation.class);
        if ( null == r ) return false;
        if ( Null.class != r.value() ) {
            if ( isKeyOfRelationMetadata(relation, r.value()) ) {
                return true;
            }
        } else {
            if ( isKeyOfRelationMetadata(relation, upperFirstChar(field.getName())) ) {
                return true;
            }
        }
        return false;
    }

    private boolean isKeyOfRelationMetadata(final RelationMetadata relation, Object key) {
        return relation.getKeySet().contains(key);
    }

    private final class TransitionMethodScanner implements MethodScanner {

        private final TransitionMetadata transition;

        public TransitionMethodScanner(final TransitionMetadata transition) {
            this.transition = transition;
        }

        private ArrayList<Method> transitionMethodList = new ArrayList<Method>();

        @Override
        public boolean onMethodFound(Method method, VerificationFailureSet failureSet) {
            final Transition transitionAnno = method.getAnnotation(Transition.class);
            if ( null != transitionAnno ) {
                if ( Null.class != transitionAnno.value() ) {
                    if ( transitionAnno.value().getSimpleName().equals(transition.getDottedPath().getName()) ) {
                        transitionMethodList.add(method);
                    }
                } else {
                    if ( upperFirstChar(method.getName()).equals(transition.getDottedPath().getName()) ) {
                        transitionMethodList.add(method);
                    }
                }
            }
            return false;
        }

        public Method[] getTransitionMethods() {
            return transitionMethodList.toArray(new Method[0]);
        }
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
        RelationIndicatorPropertyMethodScanner scanner = new RelationIndicatorPropertyMethodScanner();
        final VerificationFailureSet verificationFailureSet = new VerificationFailureSet();
        scanMethodsOnClasses(new Class<?>[] { klass }, verificationFailureSet, scanner);
        if ( verificationFailureSet.size() > 0 ) {
            throw new VerificationException(verificationFailureSet);
        }
    }

    private final class RelationIndicatorPropertyMethodScanner implements MethodScanner {

        @Override
        public boolean onMethodFound(Method method, VerificationFailureSet failureSet) {
            Relation relation = method.getAnnotation(Relation.class);
            if ( null == relation ) {
                return false;
            } else {
                if ( !getTemplate().hasRelation(relation.value()) ) {
                    failureSet.add(newVerificationFailure(method.getDeclaringClass().getName(),
                            Errors.LM_REFERENCE_INVALID_RELATION_INSTANCE, method.getDeclaringClass().getName(),
                            relation.value().getName(), getTemplate().getDottedPath().getAbsoluteName()));
                }
            }
            return false;
        }
    }
    private final class RelationGetterScanner implements MethodScanner {

        private RelationMetadata relationMetadata;

        public RelationGetterScanner(RelationMetadata relation) {
            this.relationMetadata = relation;
        }

        public boolean covered = false;

        @Override
        public boolean onMethodFound(Method method, VerificationFailureSet failureSet) {
            if ( method.getName().startsWith("get") ) {
                Relation relation = method.getAnnotation(Relation.class);
                if ( null != relation ) {
                    if ( Null.class == relation.value() ) {
                        if ( isKeyOfRelationMetadata(relationMetadata, method.getName().substring(3)) ) {
                            covered = true;
                            return true;
                        }
                    } else {
                        if ( isKeyOfRelationMetadata(relationMetadata, relation.value()) ) {
                            covered = true;
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        public boolean isCovered() {
            return covered;
        }
    }

    private void verifyRelationInstanceOnFieldNotBeyondStateMachine(Class<?> klass) throws VerificationException {
        if ( klass.isInterface() || Object.class == klass || null == klass ) {
            return;
        }
        for ( Field field : klass.getDeclaredFields() ) {
            Relation relation = field.getAnnotation(Relation.class);
            if ( null == relation ) {
                continue;
            }
            Class<?> relationClass = relation.value();
            if ( !getTemplate().hasRelation(relationClass) ) {
                throw new VerificationException(newVerificationFailure(getDottedPath(),
                        Errors.LM_REFERENCE_INVALID_RELATION_INSTANCE, klass.getName(), relationClass.getName(),
                        getTemplate().getDottedPath().getAbsoluteName()));
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

    private void checkRelationInstanceWhetherExists(Class<?> klass, final Set<Class<?>> relations,
            final Relation relation) throws VerificationException {
        if ( null != relation ) {
            if ( relations.contains(relation.value()) ) {
                throw newVerificationException(getDottedPath(), Errors.LM_RELATION_INSTANCE_MUST_BE_UNIQUE,
                        klass.getName(), relation.value().getName());
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
                throw newVerificationException(getDottedPath(),
                        Errors.STATE_INDICATOR_CANNOT_FIND_DEFAULT_AND_SPECIFIED_STATE_INDICATOR, klass);
            } else {
                verifyStateIndicatorElement(klass, defaultGetter, defaultGetter.getReturnType());
            }
        }
    }

    private Method findCustomizedStateIndicatorGetter(Class<?> klass) throws VerificationException {
        final StateIndicatorGetterMethodScanner scanner = new StateIndicatorGetterMethodScanner(klass);
        final VerificationFailureSet failureSet = new VerificationFailureSet();
        scanMethodsOnClasses(new Class<?>[] { klass }, failureSet, scanner);
        if ( failureSet.size() > 0 )
            throw new VerificationException(failureSet);
        else {
            final Method specifiedGetter = scanner.getStateGetterMethod();
            return specifiedGetter;
        }
    }

    private void verifyStateIndicatorElement(Class<?> klass, AnnotatedElement getter, Class<?> stateType)
            throws VerificationException {
        verifyStateIndicatorElementSetterVisibility(klass, getter, stateType);
        if ( stateType.equals(java.lang.String.class) ) {
            return;
        }
        verifyStateIndicatorConverter(getter, stateType);
    }

    private void verifyStateIndicatorConverter(AnnotatedElement getter, Class<?> stateType)
            throws VerificationException {
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
            throw newVerificationException(getDottedPath(), Errors.STATE_INDICATOR_CONVERTER_NOT_FOUND,
                    getterDeclaringClass, stateType);
        } else {
            Type[] genericInterfaces = converterMeta.value().getGenericInterfaces();
            for ( Type type : genericInterfaces ) {
                if ( type instanceof ParameterizedType ) {
                    ParameterizedType pType = (ParameterizedType) type;
                    if ( pType.getRawType() instanceof Class
                            && StateConverter.class.isAssignableFrom((Class<?>) pType.getRawType()) ) {
                        if ( !stateType.equals(pType.getActualTypeArguments()[0]) ) {
                            throw newVerificationException(getDottedPath(), Errors.STATE_INDICATOR_CONVERTER_INVALID,
                                    getterDeclaringClass, stateType, converterMeta.value(),
                                    pType.getActualTypeArguments()[0]);
                        }
                    }
                } else {
                    continue;
                }
            }
        }
    }

    private void verifyStateIndicatorElementSetterVisibility(final Class<?> klass, AnnotatedElement getter,
            Class<?> returnType) throws VerificationException {
        if ( getter instanceof Method ) {
            final String getterName = ( (Method) getter ).getName();
            final String setterName = convertSetterName(getterName, returnType);
            final Method setter = findMethod(klass, setterName, returnType);
            if ( null == setter && !klass.isInterface() ) {
                throw newVerificationException(getDottedPath(), Errors.STATE_INDICATOR_SETTER_NOT_FOUND,
                        ( (Method) getter ).getDeclaringClass());
            } else {
                if ( null != setter && !Modifier.isPrivate(( setter ).getModifiers()) ) {
                    throw newVerificationException(getDottedPath(),
                            Errors.STATE_INDICATOR_CANNOT_EXPOSE_STATE_INDICATOR_SETTER, setter);
                }
            }
        } else if ( getter instanceof Field ) {
            if ( !Modifier.isPrivate(( (Field) getter ).getModifiers()) ) {
                throw newVerificationException(getDottedPath(),
                        Errors.STATE_INDICATOR_CANNOT_EXPOSE_STATE_INDICATOR_FIELD, getter);
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    private Method findMethod(Class<?> klass, String setterName, Class<?> returnType) {
        final MethodSignatureScanner scanner = new MethodSignatureScanner(setterName, new Class<?>[] { returnType });
        scanMethodsOnClasses(new Class<?>[] { klass }, null, scanner);
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
        scanMethodsOnClasses(new Class[] { klass }, null, scanner);
        return scanner.getDefaultMethod();
    }

    private Field findFieldWith(Class<?> klass, Class<? extends Annotation> aClass) {
        for ( Class<?> index = klass; index != Object.class; index = index.getSuperclass() ) {
            for ( Field field : klass.getDeclaredFields() ) {
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
        for ( TransitionMetadata transitionMetadata : getTemplate().getAllTransitions() ) {
            verifyTransitionBeCovered(klass, transitionMetadata, failureSet);
        }
    }

    private void verifyTransitionBeCovered(Class<?> klass, final TransitionMetadata transitionMetadata,
            VerificationFailureSet failureSet) {
        CoverageVerifier coverage = new CoverageVerifier(transitionMetadata);
        scanMethodsOnClasses(new Class<?>[] { klass }, failureSet, coverage);
        if ( coverage.notCovered() ) {
            failureSet.add(newVerificationFailure(transitionMetadata.getDottedPath().getAbsoluteName(),
                    Errors.LM_TRANSITION_NOT_CONCRETED_IN_LM, transitionMetadata.getDottedPath().getName(),
                    getTemplate().getDottedPath().getAbsoluteName(), klass.getSimpleName()));
        }
    }

    private void verifyTransitionMethodsValidity(final Class<?> klass, final VerificationFailureSet failureSet) {
        scanMethodsOnClasses(new Class<?>[] { klass }, failureSet, new MethodScanner() {

            @Override
            public boolean onMethodFound(Method method, VerificationFailureSet failureSet) {
                verifyTransitionMethod(method, failureSet);
                return false;
            }
        });
    }

    private final class StateIndicatorDefaultMethodScanner implements MethodScanner {

        private Method defaultStateGetterMethod = null;

        @Override
        public boolean onMethodFound(Method method, VerificationFailureSet failureSet) {
            if ( "getState".equals(method.getName()) ) {
                if ( String.class.equals(method.getReturnType()) && null == defaultStateGetterMethod ) {
                    defaultStateGetterMethod = method;
                    return true;
                } else if ( null != method.getAnnotation(Converter.class) && null == defaultStateGetterMethod ) {
                    defaultStateGetterMethod = method;
                    return true;
                }
            }
            return false;
        }

        public Method getDefaultMethod() {
            return defaultStateGetterMethod;
        }
    }
    private final class StateIndicatorGetterMethodScanner implements MethodScanner {

        private Method stateGetterMethod = null;
        private boolean overridingFound = false;
        private final Class<?> klass;

        public StateIndicatorGetterMethodScanner(Class<?> klass) {
            this.klass = klass;
        }

        @Override
        public boolean onMethodFound(Method method, VerificationFailureSet failureSet) {
            if ( null == stateGetterMethod && null != method.getAnnotation(StateIndicator.class) ) {
                stateGetterMethod = method;
                overridingFound = null != method.getAnnotation(Overrides.class);
                return false;
            } else if ( null != stateGetterMethod && null != method.getAnnotation(StateIndicator.class) ) {
                if ( !overridingFound ) {
                    failureSet.add(newVerificationException(getDottedPath(),
                            Errors.STATE_INDICATOR_MULTIPLE_STATE_INDICATOR_ERROR, klass));
                    return true;
                }
            }
            return false;
        }

        public Method getStateGetterMethod() {
            return stateGetterMethod;
        }
    }
    private final class MethodSignatureScanner implements MethodScanner {

        private Method targetMethod = null;
        private String targetMethodName = null;
        private Class<?>[] parameterTypes = null;

        public MethodSignatureScanner(String setterName, Class<?>[] classes) {
            this.targetMethodName = setterName;
            this.parameterTypes = classes;
        }

        @Override
        public boolean onMethodFound(Method method, VerificationFailureSet failureSet) {
            if ( null == targetMethod && targetMethodName.equals(method.getName())
                    && Arrays.equals(method.getParameterTypes(), parameterTypes) ) {
                targetMethod = method;
                return true;
            }
            return false;
        }

        public Method getMethod() {
            return targetMethod;
        }
    }
    private final class CoverageVerifier implements MethodScanner {

        private final TransitionMetadata transitionMetadata;
        HashSet<Class<?>> declaringClass = new HashSet<>();

        private CoverageVerifier(TransitionMetadata transitionMetadata) {
            this.transitionMetadata = transitionMetadata;
        }

        public boolean notCovered() {
            return declaringClass.size() == 0;
        }

        @Override
        public boolean onMethodFound(Method method, VerificationFailureSet failureSet) {
            if ( !match(transitionMetadata, method) ) {
                return false;
            }
            if ( !declaringClass.contains(method.getDeclaringClass()) ) {
                declaringClass.add(method.getDeclaringClass());
                return false;
            }
            final TransitionTypeEnum type = transitionMetadata.getType();
            if ( isUniqueTransition(type) ) {
                failureSet.add(newVerificationFailure(transitionMetadata.getDottedPath(),
                        Errors.LM_REDO_CORRUPT_RECOVER_TRANSITION_HAS_ONLY_ONE_METHOD, transitionMetadata
                                .getDottedPath().getName(), "@" + type.name(), getTemplate().getDottedPath(),
                        getDottedPath().getAbsoluteName()));
            }
            return false;
        }

        private boolean isUniqueTransition(final TransitionTypeEnum type) {
            return type == TransitionTypeEnum.Corrupt || type == TransitionTypeEnum.Recover
                    || type == TransitionTypeEnum.Redo;
        }

        private boolean match(TransitionMetadata transitionMetadata, Method transitionMethod) {
            Transition transition = transitionMethod.getAnnotation(Transition.class);
            if ( null == transition ) return false;
            final String transitionName = transitionMetadata.getDottedPath().getName();
            if ( Null.class == transition.value() ) {
                return transitionName.equals(upperFirstChar(transitionMethod.getName()));
            } else {
                return transitionName.equals(transition.value().getSimpleName());
            }
        }
    }
    private interface MethodScanner {

        boolean onMethodFound(Method method, VerificationFailureSet failureSet);
    }

    private void scanMethodsOnClasses(Class<?>[] klasses, final VerificationFailureSet failureSet,
            final MethodScanner scanner) {
        if ( 0 == klasses.length ) return;
        final ArrayList<Class<?>> superclasses = new ArrayList<Class<?>>();
        for ( Class<?> klass : klasses ) {
            if ( klass == Object.class ) continue;
            for ( Method method : klass.getDeclaredMethods() ) {
                if ( scanner.onMethodFound(method, failureSet) ) {
                    return;
                }
            }
            if ( null != klass.getSuperclass() && Object.class != klass ) {
                superclasses.add(klass.getSuperclass());
            }
            for ( Class<?> interfaze : klass.getInterfaces() ) {
                superclasses.add(interfaze);
            }
        }
        scanMethodsOnClasses(superclasses.toArray(new Class<?>[superclasses.size()]), failureSet, scanner);
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
            transitionMetadata = verifyTransitionMethodWithTransitionClassKey(method, failureSet, transition,
                    transitionMetadata);
        }
        if ( null != transitionMetadata ) {
            verifySpecialTransitionMethodHasZeroArgument(method, failureSet, transitionMetadata);
            configureTransitionInst(method, transitionMetadata);
        }
    }

    private void configureTransitionInst(final Method method, final TransitionMetadata transitionMetadata) {
        final TransitionInstBuilderImpl transitionInstBuilder = new TransitionInstBuilderImpl(this, method,
                transitionMetadata);
        final Iterator<Object> iterator = transitionInstBuilder.getKeySet().iterator();
        while ( iterator.hasNext() ) {
            this.transitionInsts.put(iterator.next(), transitionInstBuilder.getMetaData());
        }
        if ( null == transitionMetadataInstsMap.get(transitionMetadata) ) {
            final LinkedList<TransitionInst> insts = new LinkedList<>();
            insts.add(transitionInstBuilder.getMetaData());
            transitionMetadataInstsMap.put(transitionMetadata, insts);
        } else {
            transitionMetadataInstsMap.get(transitionMetadata).add(transitionInstBuilder.getMetaData());
        }
    }

    private TransitionMetadata verifyTransitionMethodWithTransitionClassKey(Method method,
            VerificationFailureSet failureSet, final Transition transition, TransitionMetadata transitionMetadata) {
        if ( !getTemplate().hasTransition(transition.value()) ) {
            failureSet.add(newVerificationFailure(getMethodDottedPath(method),
                    Errors.LM_TRANSITION_METHOD_WITH_INVALID_TRANSITION_REFERENCE, transition, method.getName(), method
                            .getDeclaringClass().getName(), getTemplate().getDottedPath()));
        } else {
            transitionMetadata = getTemplate().getTransition(transition.value());
        }
        return transitionMetadata;
    }

    private TransitionMetadata verifyTransitionMethodDefaultStyle(Method method, VerificationFailureSet failureSet,
            TransitionMetadata transitionMetadata) {
        if ( !getTemplate().hasTransition(upperFirstChar(method.getName())) ) {
            failureSet.add(newVerificationFailure(getMethodDottedPath(method), Errors.LM_METHOD_NAME_INVALID,
                    getTemplate().getDottedPath(), method.getName(), method.getDeclaringClass().getName()));
        } else {
            transitionMetadata = getTemplate().getTransition(upperFirstChar(method.getName()));
        }
        return transitionMetadata;
    }

    private void verifySpecialTransitionMethodHasZeroArgument(Method method, VerificationFailureSet failureSet,
            TransitionMetadata transitionMetadata) {
        switch (transitionMetadata.getType()) {
            case Corrupt:
                // fall through
            case Recover:
                // fall through
            case Redo:
                if ( method.getParameterTypes().length > 0 ) {
                    failureSet.add(newVerificationFailure(transitionMetadata.getDottedPath(),
                            Errors.TRANSITION_TYPE_CORRUPT_RECOVER_REDO_REQUIRES_ZERO_PARAMETER, method,
                            upperFirstChar(method.getName()), transitionMetadata.getType()));
                }
                break;
            default:
                break;
        }
    }

    private String getMethodDottedPath(Method method) {
        return method.getDeclaringClass().getName() + "." + method.getName();
    }

    private String upperFirstChar(String name) {
        if ( name.length() > 1 ) {
            return name.substring(0, 1).toUpperCase().concat(name.substring(1));
        } else if ( name.length() == 1 ) {
            return name.toUpperCase();
        }
        return name;
    }

    @Override
    public TransitionInst[] getTransitionSet() {
        if ( null != this.transitionInsts.values() ) {
            return this.transitionInsts.values().toArray(new TransitionInst[0]);
        } else {
            return new TransitionInst[0];
        }
    }

    @Override
    public boolean hasTransition(Object transitionKey) {
        return this.transitionInsts.containsKey(transitionKey);
    }

    @Override
    public TransitionInst getTransition(Object transitionKey) {
        return this.transitionInsts.get(transitionKey);
    }

    @Override
    public StateInst[] getStateSet() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StateInst getState(Object stateKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Method stateGetter() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Method stateSetter() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StateMachineMetaBuilder getTemplate() {
        return template;
    }

    @Override
    public StateMachineInstBuilder build(Class<?> klass, StateMachineMetaBuilder parent) throws VerificationException {
        return this;
    }
}
