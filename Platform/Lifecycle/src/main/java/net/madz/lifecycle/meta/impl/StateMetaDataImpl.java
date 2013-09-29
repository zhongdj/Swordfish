package net.madz.lifecycle.meta.impl;

import net.madz.common.DottedPath;
import net.madz.common.Dumper;
import net.madz.lifecycle.annotations.typed.ITypedReactiveObject;
import net.madz.lifecycle.annotations.typed.ITypedState;
import net.madz.lifecycle.annotations.typed.ITypedTransition;
import net.madz.lifecycle.meta.StateMachineMetaData;
import net.madz.lifecycle.meta.StateMetaData;
import net.madz.lifecycle.meta.TransitionMetaData;
import net.madz.lifecycle.meta.TransitionMetaData.TransitionTypeEnum;
import net.madz.meta.MetaData;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailureSet;

public class StateMetaDataImpl<R extends ITypedReactiveObject, S extends ITypedState<R, S>> implements MetaData, StateMetaData<R, S> {

    private final DottedPath path;

    private final S state;

    private final StateTypeEnum type;

    private final StateMachineMetaData<R, S, ?> parent;

    public StateMetaDataImpl(StateMachineMetaData<R, S, ? extends ITypedTransition> parent, S state, StateTypeEnum type, String name) {
        super();
        this.parent = parent;
        this.state = state;
        this.type = type;
        this.path = parent.getDottedPath().append(name);
    }

    @Override
    public S getState() {
        return state;
    }

    @Override
    public StateTypeEnum getType() {
        return type;
    }

    @Override
    public boolean illegalTransition(ITypedTransition transition) {
        return !state.getTransitionFunction().containsKey(transition);
    }

    @Override
    public S nextState(ITypedTransition transition) {
        return (S) state.getTransitionFunction().get(transition);
    }

    @Override
    public boolean containsCorruptTransition() {
        ITypedTransition transition = parent.getCorruptTransition().getTransition();
        return !illegalTransition(transition);
    }

    @Override
    public TransitionMetaData getCorruptTransitionMetaData() {
        for (ITypedTransition transition : state.getOutboundTransitions()) {
            TransitionMetaData transitionMetaData = parent.getTransitionMetaData(transition);
            if (transitionMetaData.getType() == TransitionTypeEnum.Corrupt) {
                return transitionMetaData;
            }
        }
        return null;
    }

    @Override
    public MetaData getParent() {
        return parent;
    }

    @Override
    public DottedPath getDottedPath() {
        return path;
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {

        if (getType() == StateTypeEnum.Running && (!containsCorruptTransition() || containsRecoverTransition())) {
            verificationSet.add(new VerificationException(this, "RunningStateWithoutCorruptTransition",
                    "Each Running State Must Contains One Corrupt Transition: " + getDottedPath().getAbsoluteName()));
        }

    }

    @Override
    public boolean containsRecoverTransition() {
        return null != getRecoverTransitionMetaData();
    }

    @Override
    public void dump(Dumper dumper) {
        dumper.dump(toString());
    }

    @Override
    public String toString() {
        return "StateMetaDataImpl [path=" + path + ", state=" + state + ", type=" + type + "]";
    }

    @Override
    public TransitionMetaData getRecoverTransitionMetaData() {
        for (ITypedTransition transition : state.getOutboundTransitions()) {
            TransitionMetaData transitionMetaData = parent.getTransitionMetaData(transition);
            if (transitionMetaData.getType() == TransitionTypeEnum.Recover) {
                return transitionMetaData;
            }
        }
        for (ITypedTransition transition : state.getOutboundTransitions()) {
            TransitionMetaData transitionMetaData = parent.getTransitionMetaData(transition);
            if (transitionMetaData.getType() == TransitionTypeEnum.Redo) {
                return transitionMetaData;
            }
        }
        return null;
    }

}
