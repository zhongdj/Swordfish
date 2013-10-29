package net.madz.lifecycle.meta.impl.builder;

import net.madz.lifecycle.meta.builder.ConditionMetaBuilder;
import net.madz.lifecycle.meta.builder.StateMachineMetaBuilder;
import net.madz.lifecycle.meta.template.ConditionMetadata;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.verification.VerificationFailureSet;

public class ConditionMetaBuilderImpl extends AnnotationBasedMetaBuilder<ConditionMetadata, StateMachineMetadata>
        implements ConditionMetaBuilder {

    protected ConditionMetaBuilderImpl(StateMachineMetadata parent, String name) {
        super(parent, name);
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {}

    @Override
    public ConditionMetaBuilder build(Class<?> klass, StateMachineMetaBuilder builder) {
        return this;
    }
}
