package net.madz.lifecycle.meta;

import java.lang.reflect.AnnotatedElement;

import net.madz.meta.MetaDataBuilder;

public interface TransitionMetaDataBuilder extends MetaDataBuilder<TransitionMetaData, StateMachineMetaData<?, ?, ?>> {

    TransitionMetaData build(StateMachineMetaData<?, ?, ?> parent, AnnotatedElement element);

}