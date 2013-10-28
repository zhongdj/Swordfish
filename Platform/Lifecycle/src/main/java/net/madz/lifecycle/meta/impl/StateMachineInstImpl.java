package net.madz.lifecycle.meta.impl;

import java.lang.reflect.Method;

import net.madz.common.DottedPath;
import net.madz.lifecycle.meta.instance.StateInst;
import net.madz.lifecycle.meta.instance.StateMachineInst;
import net.madz.lifecycle.meta.instance.TransitionInst;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.meta.MetaData;
import net.madz.verification.VerificationFailureSet;


public class StateMachineInstImpl implements StateMachineInst {

    @Override
    public StateMachineMetadata getTemplate() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DottedPath getDottedPath() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MetaData getParent() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {
        // TODO Auto-generated method stub
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
}
