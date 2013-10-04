package net.madz.lifecycle.meta.builder;

import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.meta.MetaData;
import net.madz.meta.MetaDataBuilder;

public interface StateMachineMetaBuilder extends MetaDataBuilder<StateMachineMetadata, MetaData>, StateMachineMetadata {

    MetaDataBuilder<StateMachineMetadata, StateMachineMetadata> build(Class<?> clazz);
}
