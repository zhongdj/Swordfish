package net.madz.lifecycle.meta;

import java.lang.reflect.AnnotatedElement;

import net.madz.lifecycle.IReactiveObject;
import net.madz.lifecycle.IState;
import net.madz.meta.MetaDataBuilder;

public interface StateMetaDataBuilder<R extends IReactiveObject, S extends IState<R, S>> extends
        MetaDataBuilder<StateMetaData<R, S>, StateMachineMetaData<R, S, ?>> {

    StateMetaData<R, S> build(StateMachineMetaData<R, S, ?> parent, AnnotatedElement element);

}