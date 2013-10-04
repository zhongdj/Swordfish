package net.madz.lifecycle.meta.builder;

import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.lifecycle.meta.template.TransitionMetadata;
import net.madz.meta.MetaDataBuilder;

public interface TransitionMetaBuilder extends MetaDataBuilder<TransitionMetadata, StateMachineMetadata> {

    TransitionMetaBuilder build(Class<?> clazz, StateMachineMetaBuilder parent);
}
