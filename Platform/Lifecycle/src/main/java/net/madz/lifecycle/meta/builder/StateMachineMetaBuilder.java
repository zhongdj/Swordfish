package net.madz.lifecycle.meta.builder;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.meta.MetaDataBuilder;
import net.madz.verification.VerificationException;

public interface StateMachineMetaBuilder extends MetaDataBuilder<StateMachineMetadata, StateMachineMetadata>,
        StateMachineMetadata {

    StateMachineMetaBuilder build(Class<?> clazz) throws VerificationException;

    void setRegistry(AbsStateMachineRegistry registry);

    void setComposite(boolean b);

    void setOwningState(StateMetaBuilder stateMetaBuilderImpl);
}
