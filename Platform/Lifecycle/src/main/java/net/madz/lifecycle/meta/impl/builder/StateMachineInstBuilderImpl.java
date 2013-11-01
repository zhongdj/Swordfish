package net.madz.lifecycle.meta.impl.builder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;

import net.madz.lifecycle.Errors;
import net.madz.lifecycle.annotations.Null;
import net.madz.lifecycle.annotations.Transition;
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
                    Errors.LM_TRANSITION_NOT_CONCRETED_IN_LM, transitionMetadata.getDottedPath()
                            .getName(), getTemplate().getDottedPath().getAbsoluteName(),klass.getSimpleName()));
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
                                .getDottedPath().getName(), getTemplate().getDottedPath(), getDottedPath()
                                .getAbsoluteName(), "@" + type.name()));
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
