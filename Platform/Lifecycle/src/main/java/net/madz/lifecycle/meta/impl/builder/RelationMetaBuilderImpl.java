package net.madz.lifecycle.meta.impl.builder;

import java.util.ArrayList;

import net.madz.lifecycle.meta.builder.RelationMetaBuilder;
import net.madz.lifecycle.meta.builder.StateMachineMetaBuilder;
import net.madz.lifecycle.meta.instance.RelationInst;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.lifecycle.meta.template.StateMetadata;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailureSet;

public class RelationMetaBuilderImpl extends AnnotationMetaBuilderBase<RelationMetaBuilder, StateMachineMetaBuilder>
        implements RelationMetaBuilder {

    private StateMachineMetadata relatedStateMachine;
    private final ArrayList<StateMetadata> relatedStateSet = new ArrayList<>();

    protected RelationMetaBuilderImpl(StateMachineMetaBuilder parent, String name) {
        super(parent, name);
    }

    @Override
    public StateMachineMetadata getRelatedStateMachine() {
        return relatedStateMachine;
    }

    @Override
    public StateMetadata[] getRelatedStateSet() {
        return relatedStateSet.toArray(new StateMetadata[relatedStateSet.size()]);
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {
        // TODO Auto-generated method stub
    }

    @Override
    public RelationInst newInstance(Class<?> clazz) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RelationMetaBuilder build(Class<?> klass, StateMachineMetaBuilder parent) throws VerificationException {
        // TODO Auto-generated method stub
        return null;
    }
}
