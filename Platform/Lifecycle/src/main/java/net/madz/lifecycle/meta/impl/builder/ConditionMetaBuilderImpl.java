package net.madz.lifecycle.meta.impl.builder;

import net.madz.lifecycle.meta.builder.ConditionMetaBuilder;
import net.madz.lifecycle.meta.builder.StateMachineMetaBuilder;
import net.madz.verification.VerificationFailureSet;

public class ConditionMetaBuilderImpl extends AnnotationMetaBuilderBase<ConditionMetaBuilder, StateMachineMetaBuilder>
        implements ConditionMetaBuilder {

    protected ConditionMetaBuilderImpl(StateMachineMetaBuilder parent, String name) {
        super(parent, name);
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {}

    @Override
    public ConditionMetaBuilder build(Class<?> klass, StateMachineMetaBuilder builder) {
        addKeys(klass);
        return this;
    }
}
