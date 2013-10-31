package net.madz.lifecycle.meta.builder;

import net.madz.lifecycle.meta.template.StateMetadata;
import net.madz.meta.MetaDataBuilder;
import net.madz.verification.VerificationException;

public interface StateMetaBuilder extends MetaDataBuilder<StateMetaBuilder, StateMachineMetaBuilder>, StateMetadata {

    StateMetaBuilder build(Class<?> clazz, StateMachineMetaBuilder parent) throws VerificationException;

    @Override
    StateMachineMetaBuilder getStateMachine();

    void configureFunctions(Class<?> stateClass) throws VerificationException;

    void configureCompositeStateMachine(Class<?> stateClass) throws VerificationException;

    void configureRelations(Class<?> clazz) throws VerificationException;
}
