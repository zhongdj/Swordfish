package net.madz.lifecycle.meta.builder;

import net.madz.lifecycle.meta.template.ConditionMetadata;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.verification.VerificationException;

public interface ConditionMetaBuilder extends AnnotationMetaBuilder<ConditionMetadata, StateMachineMetadata>,
        ConditionMetadata {

    @Override
    ConditionMetaBuilder build(Class<?> klass, StateMachineMetadata parent) throws VerificationException;
}
