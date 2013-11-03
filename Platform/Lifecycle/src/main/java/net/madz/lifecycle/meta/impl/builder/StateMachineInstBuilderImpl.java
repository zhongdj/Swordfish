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
import java.util.HashSet;

import net.madz.lifecycle.Errors;
import net.madz.lifecycle.StateConverter;
import net.madz.lifecycle.annotations.Null;
import net.madz.lifecycle.annotations.StateIndicator;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.annotations.state.Converter;
import net.madz.lifecycle.meta.builder.StateMachineInstBuilder;
import net.madz.lifecycle.meta.builder.StateMachineMetaBuilder;
import net.madz.lifecycle.meta.instance.StateInst;
import net.madz.lifecycle.meta.instance.TransitionInst;
import net.madz.lifecycle.meta.template.TransitionMetadata;
import net.madz.lifecycle.meta.template.TransitionMetadata.TransitionTypeEnum;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailureSet;

public class StateMachineInstBuilderImpl extends
        AnnotationMetaBuilderBase<StateMachineInstBuilder, StateMachineMetaBuilder> implements StateMachineInstBuilder {

    private StateMachineMetaBuilder template;

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

    private Method findCustomizedStateIndicatorGetter(Class<?> klass) {
        StateIndicatorGetterMethodScanner scanner = new StateIndicatorGetterMethodScanner();
        scanMethodsOnClasses(new Class<?>[] { klass }, null, scanner);
        final Method specifiedGetter = scanner.getStateGetterMethod();
        return specifiedGetter;
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

    private Field findFieldWith(Class<?> klass, Class<StateIndicator> aClass) {
        for ( Class<?> index = klass; index != Object.class; index = index.getSuperclass() ) {
            for ( Field field : klass.getDeclaredFields() ) {
                if ( null != field.getAnnotation(StateIndicator.class) ) {
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
            public void onMethodFound(Method method, VerificationFailureSet failureSet) {
                verifyTransitionMethod(method, failureSet);
            }
        });
    }

    private final class StateIndicatorDefaultMethodScanner implements MethodScanner {

        private Method defaultStateGetterMethod = null;

        @Override
        public void onMethodFound(Method method, VerificationFailureSet failureSet) {
            if ( "getState".equals(method.getName()) ) {
                if ( String.class.equals(method.getReturnType()) && null == defaultStateGetterMethod ) {
                    defaultStateGetterMethod = method;
                } else if ( null != method.getAnnotation(Converter.class) && null == defaultStateGetterMethod ) {
                    defaultStateGetterMethod = method;
                }
            }
        }

        public Method getDefaultMethod() {
            return defaultStateGetterMethod;
        }
    }
    private final class StateIndicatorGetterMethodScanner implements MethodScanner {

        private Method stateGetterMethod = null;

        @Override
        public void onMethodFound(Method method, VerificationFailureSet failureSet) {
            if ( null == stateGetterMethod && null != method.getAnnotation(StateIndicator.class) ) {
                stateGetterMethod = method;
            }
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
        public void onMethodFound(Method method, VerificationFailureSet failureSet) {
            if ( null == targetMethod && targetMethodName.equals(method.getName())
                    && Arrays.equals(method.getParameterTypes(), parameterTypes) ) {
                targetMethod = method;
            }
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
        public void onMethodFound(Method method, VerificationFailureSet failureSet) {
            if ( !match(transitionMetadata, method) ) {
                return;
            }
            if ( !declaringClass.contains(method.getDeclaringClass()) ) {
                declaringClass.add(method.getDeclaringClass());
                return;
            }
            final TransitionTypeEnum type = transitionMetadata.getType();
            if ( isUniqueTransition(type) ) {
                failureSet.add(newVerificationFailure(transitionMetadata.getDottedPath(),
                        Errors.LM_REDO_CORRUPT_RECOVER_TRANSITION_HAS_ONLY_ONE_METHOD, transitionMetadata
                                .getDottedPath().getName(), "@" + type.name(), getTemplate().getDottedPath(),
                        getDottedPath().getAbsoluteName()));
            }
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

        void onMethodFound(Method method, VerificationFailureSet failureSet);
    }

    private void scanMethodsOnClasses(Class<?>[] klasses, final VerificationFailureSet failureSet,
            final MethodScanner scanner) {
        if ( 0 == klasses.length ) return;
        final ArrayList<Class<?>> superclasses = new ArrayList<Class<?>>();
        for ( Class<?> klass : klasses ) {
            if ( klass == Object.class ) continue;
            for ( Method method : klass.getDeclaredMethods() ) {
                scanner.onMethodFound(method, failureSet);
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
        Transition transition = method.getAnnotation(Transition.class);
        if ( transition == null ) {
            return;
        } else {
            if ( Null.class == transition.value() ) {
                if ( !getTemplate().hasTransition(upperFirstChar(method.getName())) ) {
                    failureSet.add(newVerificationFailure(getMethodDottedPath(method), Errors.LM_METHOD_NAME_INVALID,
                            getTemplate().getDottedPath(), method.getName(), method.getDeclaringClass().getName()));
                }
            } else if ( !getTemplate().hasTransition(transition.value()) ) {
                failureSet.add(newVerificationFailure(getMethodDottedPath(method),
                        Errors.LM_TRANSITION_METHOD_WITH_INVALID_TRANSITION_REFERENCE, transition, method.getName(),
                        method.getDeclaringClass().getName(), getTemplate().getDottedPath()));
            }
        }
    }

    private String getMethodDottedPath(Method method) {
        return method.getDeclaringClass().getName() + "." + method.getName();
    }

    private Annotation getMethodAnnotation(Class<Transition> class1) {
        // TODO Auto-generated method stub
        return null;
    }

    private String getMethodTransitionName(Method method) {
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasTransition(Object transitionKey) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public TransitionInst getTransition(Object transitionKey) {
        // TODO Auto-generated method stub
        return null;
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
