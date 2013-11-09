package net.madz.lifecycle.meta.impl;

import java.lang.reflect.Method;

import net.madz.common.DottedPath;
import net.madz.lifecycle.meta.instance.StateObject;
import net.madz.lifecycle.meta.instance.StateMachineObject;
import net.madz.lifecycle.meta.instance.TransitionObject;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.meta.MetaData;
import net.madz.verification.VerificationFailureSet;

public class StateMachineObjectImpl implements StateMachineObject {

    @Override
    public StateMachineMetadata getTemplate() {
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
    public TransitionObject[] getTransitionSet() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasTransition(Object transitionKey) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public TransitionObject getTransition(Object transitionKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StateObject[] getStateSet() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StateObject getState(Object stateKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StateAccessor<String> getStateAccessor() {
        return null;
    }

    @Override
    public String evaluateState(Object target) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setTargetState(Object target, String state) {
        // TODO Auto-generated method stub
    }

    @Override
    public String getNextState(Object target, Object transtionKey) {
        // TODO Auto-generated method stub
        return null;
    }
}
