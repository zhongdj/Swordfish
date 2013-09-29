package net.madz.lifecycle.meta;

import java.lang.reflect.AnnotatedElement;

import net.madz.lifecycle.annotations.typed.ITypedReactiveObject;
import net.madz.lifecycle.annotations.typed.ITypedState;
import net.madz.meta.MetaDataBuilder;

public interface StateMetaDataBuilder<R extends ITypedReactiveObject, S extends ITypedState<R, S>> extends
        MetaDataBuilder<StateMetaData<R, S>, StateMachineMetaData<R, S, ?>> {

    StateMetaData<R, S> build(StateMachineMetaData<R, S, ?> parent, AnnotatedElement element);

}