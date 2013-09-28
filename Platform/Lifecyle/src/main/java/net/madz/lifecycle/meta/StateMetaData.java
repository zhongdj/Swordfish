package net.madz.lifecycle.meta;

import net.madz.common.Dumpable;
import net.madz.lifecycle.IReactiveObject;
import net.madz.lifecycle.IState;
import net.madz.lifecycle.ITransition;
import net.madz.meta.MetaData;

public interface StateMetaData<R extends IReactiveObject, S extends IState<R, S>> extends MetaData, Dumpable {

    public static enum StateTypeEnum {
        Initial, End, Running, Stopped, Corrupted, Waiting, Unknown
    }

    S getState();

    StateTypeEnum getType();

    boolean illegalTransition(ITransition transition);

    S nextState(ITransition transition);

    boolean containsCorruptTransition();

    TransitionMetaData getCorruptTransitionMetaData();

    TransitionMetaData getRecoverTransitionMetaData();

    boolean containsRecoverTransition();
}