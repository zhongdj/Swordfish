package net.madz.lifecycle.meta;

import net.madz.common.Dumpable;
import net.madz.lifecycle.IReactiveObject;
import net.madz.lifecycle.IState;
import net.madz.lifecycle.ITransition;
import net.madz.meta.FlavorMetaData;
import net.madz.meta.MetaData;

public interface StateMachineMetaData<R extends IReactiveObject, S extends IState<R, S>, T extends ITransition> extends MetaData, FlavorMetaData<MetaData>, Dumpable {

    void addFinalState(StateMetaData<R, S> finalState);

    void addTransientState(StateMetaData<R, S> state);

    void addTransition(TransitionMetaData transition);

    StateMetaData<R, S> getStateMetaData(S state);

    T getTransition(String name);

    TransitionMetaData getTransitionMetaData(ITransition transition);

    TransitionMetaData getRedoTransition();

    TransitionMetaData getRecoverTransition();

    TransitionMetaData getCorruptTransition();

    StateMetaData<R, S> getInitialState();
}