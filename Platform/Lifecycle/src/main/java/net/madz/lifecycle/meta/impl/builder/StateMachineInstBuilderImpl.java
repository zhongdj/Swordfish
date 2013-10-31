package net.madz.lifecycle.meta.impl.builder;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.madz.lifecycle.Errors;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.meta.builder.StateMachineInstBuilder;
import net.madz.lifecycle.meta.instance.StateInst;
import net.madz.lifecycle.meta.instance.StateMachineInst;
import net.madz.lifecycle.meta.instance.TransitionInst;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.lifecycle.meta.template.TransitionMetadata;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailure;
import net.madz.verification.VerificationFailureSet;

public class StateMachineInstBuilderImpl extends AnnotationBasedMetaBuilder<StateMachineInst, StateMachineMetadata> implements StateMachineInstBuilder {

    public StateMachineInstBuilderImpl(StateMachineMetadata parent, String name) {
        super(parent, name);
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
        verifyTransitons(klass);
    }

    private void verifyTransitons(Class<?> klass) throws VerificationException {
        final VerificationFailureSet failureSet = new VerificationFailureSet();
        final TransitionMetadata[] expectedTransitionSet = this.parent.getTransitionSet();
        final Method[] declaredMethods = klass.getDeclaredMethods();
        final Map<String, Method> actualTranstionMethods = new HashMap<>();
        for ( Method method : declaredMethods ) {
            final Transition annotation = method.getAnnotation(Transition.class);
            if ( null != annotation ) {
                if ( null != annotation.value() ) {
                    actualTranstionMethods.put(annotation.value().getSimpleName(), method);
                } else {
                    actualTranstionMethods.put(upperFirstChar(method.getName()), method);
                }
            }
        }
        // Make sure all the method's transition can be found in the SM.
        for ( String transitionClassName : actualTranstionMethods.keySet() ) {
            TransitionMetadata transition = this.parent.getTransition(transitionClassName);
            Method method = actualTranstionMethods.get(transitionClassName);
            if ( null == transition ) {
                failureSet.add(newVerificationFailure(method.getName(), Errors.LM_TRANSITION_METHOD_WITH_OUTBOUNDED_TRANSITION, klass.getName(),
                        transitionClassName, method.getName(), this.parent.getDottedPath()));
            }
        }
        // Compare whether transitions are covered by methods in LM.
        // Make sure transition annotated with @Corrupt,or @Redo, or @Recover
        // has only 1 method in LM.
        
        if (failureSet.size() > 0) {
            throw new VerificationException(failureSet);
        }
    }

    private String upperFirstChar(String name) {
        // TODO Auto-generated method stub
        return null;
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
    public StateMachineMetadata getTemplate() {
        // TODO Auto-generated method stub
        return null;
    }
}
