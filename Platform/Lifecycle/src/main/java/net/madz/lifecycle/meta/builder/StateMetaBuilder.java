package net.madz.lifecycle.meta.builder;

import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.lifecycle.meta.template.StateMetadata;
import net.madz.meta.MetaDataBuilder;

public interface StateMetaBuilder extends MetaDataBuilder<StateMetadata, StateMachineMetadata> {

    StateMetaBuilder build(Class<?> clazz, StateMachineMetaBuilder parent);
}
