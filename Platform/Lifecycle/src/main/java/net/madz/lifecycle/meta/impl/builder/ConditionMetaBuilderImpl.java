package net.madz.lifecycle.meta.impl.builder;

import net.madz.common.Dumper;
import net.madz.lifecycle.meta.builder.ConditionMetaBuilder;
import net.madz.lifecycle.meta.builder.StateMachineMetaBuilder;
import net.madz.lifecycle.meta.template.ConditionMetadata;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
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
        configureSuper(klass);
        addKeys(klass);
        return this;
    }

    @Override
    public void dump(Dumper dumper) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean hasSuper(Class<?> metaClass) {
        if ( !super.hasSuper(metaClass) ) {
            return false;
        }
        try {
            ConditionMetadata superCondition = findSuper(metaClass);
            if ( null == superCondition ) {
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
}