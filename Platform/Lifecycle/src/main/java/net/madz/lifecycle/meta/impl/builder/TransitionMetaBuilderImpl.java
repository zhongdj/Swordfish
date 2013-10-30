package net.madz.lifecycle.meta.impl.builder;

import net.madz.common.Dumper;
import net.madz.lifecycle.meta.builder.StateMachineMetaBuilder;
import net.madz.lifecycle.meta.builder.TransitionMetaBuilder;
import net.madz.lifecycle.meta.instance.TransitionInst;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.lifecycle.meta.template.TransitionMetadata;
import net.madz.meta.MetaData;
import net.madz.meta.MetaDataFilter;
import net.madz.meta.MetaDataFilterable;
import net.madz.verification.VerificationFailureSet;

public class TransitionMetaBuilderImpl extends AnnotationBasedMetaBuilder<TransitionMetadata, StateMachineMetadata>
        implements TransitionMetaBuilder {

    private TransitionTypeEnum type = TransitionTypeEnum.Common;

    protected TransitionMetaBuilderImpl(StateMachineMetadata parent, String name) {
        super(parent, "TransitionSet." + name);
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {}

    @Override
    public TransitionMetaBuilder build(Class<?> clazz, StateMachineMetaBuilder parent) {
        addKeys(clazz);
        return this;
    }

    @Override
    public StateMachineMetadata getStateMachine() {
        return parent;
    }

    @Override
    public TransitionTypeEnum getType() {
        return type;
    }

    @Override
    public long getTimeout() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void dump(Dumper dumper) {
        // TODO Auto-generated method stub
    }

    @Override
    public TransitionInst newInstance(Class<?> clazz) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MetaDataFilterable filter(MetaData parent, MetaDataFilter filter, boolean lazyFilter) {
        // TODO Auto-generated method stub
        return null;
    }
}
