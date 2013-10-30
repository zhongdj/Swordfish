package net.madz.lifecycle.meta.builder;

import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.lifecycle.meta.template.StateMetadata;
import net.madz.meta.MetaDataBuilder;
import net.madz.verification.VerificationException;

public interface StateMetaBuilder extends MetaDataBuilder<StateMetadata, StateMachineMetadata>, StateMetadata {

    StateMetaBuilder build(Class<?> clazz, StateMachineMetaBuilder parent) throws VerificationException;

    void configureFunctions(Class<?> stateClass) throws VerificationException;
}
