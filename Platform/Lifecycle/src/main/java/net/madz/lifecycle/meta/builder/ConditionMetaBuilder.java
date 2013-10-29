package net.madz.lifecycle.meta.builder;

import net.madz.lifecycle.meta.template.ConditionMetadata;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.meta.MetaDataBuilder;

public interface ConditionMetaBuilder extends MetaDataBuilder<ConditionMetadata, StateMachineMetadata> {
    ConditionMetaBuilder build(Class<?> klass, StateMachineMetaBuilder builder);
}
