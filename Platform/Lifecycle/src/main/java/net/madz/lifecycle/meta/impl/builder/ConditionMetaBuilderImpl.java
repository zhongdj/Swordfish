package net.madz.lifecycle.meta.impl.builder;

import net.madz.lifecycle.meta.builder.ConditionMetaBuilder;
import net.madz.lifecycle.meta.builder.StateMachineMetaBuilder;
import net.madz.meta.MetaData;
import net.madz.meta.MetaDataFilter;
import net.madz.meta.MetaDataFilterable;
import net.madz.verification.VerificationFailureSet;

public class ConditionMetaBuilderImpl extends AnnotationMetaBuilderBase<ConditionMetaBuilder, StateMachineMetaBuilder>
        implements ConditionMetaBuilder {

    protected ConditionMetaBuilderImpl(StateMachineMetaBuilder parent, String name) {
        super(parent, "ConditionSet." + name);
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {}

    @Override
    public ConditionMetaBuilder build(Class<?> klass, StateMachineMetaBuilder builder) {
        addKeys(klass);
        return this;
    }

    @Override
    public MetaDataFilterable filter(MetaData parent, MetaDataFilter filter, boolean lazyFilter) {
        // TODO Auto-generated method stub
        return null;
    }
}
