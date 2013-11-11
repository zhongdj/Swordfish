package net.madz.lifecycle.meta.impl.builder;

import net.madz.lifecycle.meta.builder.StateMachineObjectBuilder;
import net.madz.lifecycle.meta.builder.StateObjectBuilder;
import net.madz.lifecycle.meta.template.StateMetadata;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailureSet;

public class StateObjectBuilderImpl extends ObjectBuilderBase<StateObjectBuilder, StateMachineObjectBuilder> implements
        StateObjectBuilder {

    private StateMetadata template;

    protected StateObjectBuilderImpl(StateMachineObjectBuilder parent, StateMetadata stateMetadata) {
        super(parent, "StateSet." + stateMetadata.getDottedPath().getName());
        this.template = stateMetadata;
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {}

    @Override
    public StateObjectBuilder build(Class<?> klass, StateMachineObjectBuilder parent) throws VerificationException {
        addKeys(template.getKeySet());
        return this;
    }

    @Override
    public StateMetadata getTemplate() {
        return template;
    }
}
