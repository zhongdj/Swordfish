package net.madz.lifecycle.meta.builder;

import net.madz.lifecycle.meta.template.ConditionMetadata;
import net.madz.meta.KeySet;

public interface ConditionMetaBuilder extends AnnotationMetaBuilder<ConditionMetaBuilder, StateMachineMetaBuilder>,
        ConditionMetadata {

    KeySet getKeySet();
}
