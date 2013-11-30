package net.madz.lifecycle.meta.builder.impl;

import net.madz.common.Dumper;
import net.madz.lifecycle.meta.builder.ConditionMetaBuilder;
import net.madz.lifecycle.meta.builder.StateMachineMetaBuilder;
import net.madz.lifecycle.meta.type.ConditionMetadata;
import net.madz.lifecycle.meta.type.StateMachineMetadata;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailureSet;

public class ConditionMetaBuilderImpl extends InheritableAnnotationMetaBuilderBase<ConditionMetadata, StateMachineMetadata> implements ConditionMetaBuilder {

    protected ConditionMetaBuilderImpl(StateMachineMetaBuilder parent, String name) {
        super(parent, "ConditionSet." + name);
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {}

    @Override
    public ConditionMetaBuilder build(Class<?> klass, StateMachineMetadata builder) throws VerificationException {
        super.build(klass, builder);
        configureSuper(klass);
        return this;
    }

    @Override
    public void dump(Dumper dumper) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean hasSuper(Class<?> metaClass) {
        if ( !super.hasSuper(metaClass) ) return false;
        try {
            if ( null == findSuper(metaClass) ) {
                return false;
            } else {
                return true;
            }
        } catch (VerificationException e) {
            return false;
        }
    }

    @Override
    protected ConditionMetadata findSuper(Class<?> metaClass) throws VerificationException {
        return parent.getSuper().getCondtion(metaClass);
    }

    @Override
    protected boolean extendsSuperKeySet() {
        return true;
    }
}