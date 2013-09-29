package net.madz.lifecycle.meta;

import net.madz.common.Dumpable;
import net.madz.lifecycle.annotations.typed.ITypedReactiveObject;
import net.madz.lifecycle.annotations.typed.ITypedState;
import net.madz.lifecycle.annotations.typed.ITypedTransition;
import net.madz.meta.FlavorMetaData;
import net.madz.meta.MetaData;

public interface StateMachineMetaData<R extends ITypedReactiveObject, S extends ITypedState<R, S>, T extends ITypedTransition> extends MetaData, FlavorMetaData<MetaData>, Dumpable {

    void addFinalState(StateMetaData<R, S> finalState);

    void addTransientState(StateMetaData<R, S> state);

    void addTransition(TransitionMetaData transition);

    StateMetaData<R, S> getStateMetaData(S state);

    T getTransition(String name);

    TransitionMetaData getTransitionMetaData(ITypedTransition transition);

    TransitionMetaData getRedoTransition();

    TransitionMetaData getRecoverTransition();

    TransitionMetaData getCorruptTransition();

    StateMetaData<R, S> getInitialState();
}