package net.madz.lifecycle.meta.typed;

import net.madz.common.Dumpable;
import net.madz.lifecycle.annotations.typed.ITypedReactiveObject;
import net.madz.lifecycle.annotations.typed.ITypedState;
import net.madz.lifecycle.annotations.typed.ITypedTransition;
import net.madz.meta.MetaData;

public interface StateMetaData<R extends ITypedReactiveObject, S extends ITypedState<R, S>> extends MetaData, Dumpable {

    public static enum StateTypeEnum {
        Initial, End, Running, Stopped, Corrupted, Waiting, Unknown
    }

    S getState();

    StateTypeEnum getType();

    boolean illegalTransition(ITypedTransition transition);

    S nextState(ITypedTransition transition);

    boolean containsCorruptTransition();

    TransitionMetaData getCorruptTransitionMetaData();

    boolean containsRecoverTransition();

    TransitionMetaData getRecoverTransitionMetaData();

}