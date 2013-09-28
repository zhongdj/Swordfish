package net.madz.lifecycle.meta.impl;

import java.lang.reflect.Method;

import net.madz.common.DottedPath;
import net.madz.common.Dumper;
import net.madz.lifecycle.ITransition;
import net.madz.lifecycle.meta.StateMachineMetaData;
import net.madz.lifecycle.meta.TransitionMetaData;
import net.madz.meta.MetaData;
import net.madz.verification.VerificationFailureSet;

public class TransitionMetaDataImpl implements MetaData, TransitionMetaData {

    protected final TransitionTypeEnum type;
    protected final ITransition transition;
    protected final long timeout;
    protected final StateMachineMetaData<?, ?, ?> parent;
    protected final DottedPath dottedPath;
    protected Method transitionMethod;

    public TransitionMetaDataImpl(StateMachineMetaData<?, ?, ?> parent, String name, TransitionTypeEnum type, ITransition transition, long timeout) {
        super();
        this.parent = parent;
        this.dottedPath = parent.getDottedPath().append(name);
        this.type = type;
        this.transition = transition;
        this.timeout = timeout;
    }

    @Override
    public TransitionTypeEnum getType() {
        return type;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ITransition> T getTransition() {
        return (T) transition;
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public StateMachineMetaData<?, ?, ?> getParent() {
        return parent;
    }

    @Override
    public Method getTransitionMethod() {
        return this.transitionMethod;
    }

    public void setTransitionMethod(Method method) {
        this.transitionMethod = method;
    }

    @Override
    public DottedPath getDottedPath() {
        return dottedPath;
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {
    }

    @Override
    public void dump(Dumper dumper) {
        dumper.dump(toString());
    }

    @Override
    public String toString() {
        return "TransitionMetaDataImpl [dottedPath=" + dottedPath + ", type=" + type + ", transition=" + transition + ", timeout=" + timeout
                + ", transitionMethod=" + transitionMethod + "]";
    }

}
