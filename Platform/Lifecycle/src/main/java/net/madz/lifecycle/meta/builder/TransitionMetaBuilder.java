package net.madz.lifecycle.meta.builder;

import net.madz.lifecycle.meta.template.TransitionMetadata;

public interface TransitionMetaBuilder extends
        AnnotationMetaBuilder<TransitionMetaBuilder, StateMachineMetaBuilder>, TransitionMetadata {}
