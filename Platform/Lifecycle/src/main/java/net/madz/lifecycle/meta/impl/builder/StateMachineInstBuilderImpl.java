package net.madz.lifecycle.meta.impl.builder;

import java.lang.reflect.Method;

import net.madz.lifecycle.meta.builder.StateMachineInstBuilder;
import net.madz.lifecycle.meta.instance.StateInst;
import net.madz.lifecycle.meta.instance.StateMachineInst;
import net.madz.lifecycle.meta.instance.TransitionInst;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.verification.VerificationFailureSet;

public class StateMachineInstBuilderImpl extends AnnotationBasedMetaBuilder<StateMachineInst, StateMachineMetadata> implements StateMachineInstBuilder {

    public StateMachineInstBuilderImpl(StateMachineMetadata parent, String name) {
        super(parent, name);
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {
        // TODO Auto-generated method stub
    }

    public StateMachineInstBuilder build(Class<?> klass) {
        return this;
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
