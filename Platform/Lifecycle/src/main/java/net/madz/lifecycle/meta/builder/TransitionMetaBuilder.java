package net.madz.lifecycle.meta.builder;

import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.lifecycle.meta.template.TransitionMetadata;
import net.madz.verification.VerificationException;

public interface TransitionMetaBuilder extends AnnotationMetaBuilder<TransitionMetadata, StateMachineMetadata>, TransitionMetadata {

    @Override
    TransitionMetaBuilder build(Class<?> klass, StateMachineMetadata parent) throws VerificationException;
}
