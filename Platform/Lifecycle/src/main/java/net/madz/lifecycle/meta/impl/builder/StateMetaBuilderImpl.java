package net.madz.lifecycle.meta.impl.builder;

import net.madz.common.Dumper;
import net.madz.lifecycle.meta.builder.StateMachineMetaBuilder;
import net.madz.lifecycle.meta.builder.StateMetaBuilder;
import net.madz.lifecycle.meta.template.RelationMetadata;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.lifecycle.meta.template.StateMetadata;
import net.madz.lifecycle.meta.template.TransitionMetadata;
import net.madz.meta.MetaData;
import net.madz.meta.MetaDataFilter;
import net.madz.meta.MetaDataFilterable;
import net.madz.verification.VerificationFailureSet;

public class StateMetaBuilderImpl extends AnnotationBasedMetaBuilder<StateMetadata, StateMachineMetadata> implements
        StateMetaBuilder, StateMetadata {

    protected StateMetaBuilderImpl(StateMachineMetadata parent, String name) {
        super(parent, name);
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean hasRedoTransition() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public TransitionMetadata getRedoTransition() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasRecoverTransition() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public TransitionMetadata getRecoverTransition() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasCorruptTransition() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public TransitionMetadata getCorruptTransition() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MetaDataFilterable filter(MetaData parent, MetaDataFilter filter, boolean lazyFilter) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void dump(Dumper dumper) {
        // TODO Auto-generated method stub
    }

    @Override
    public StateMachineMetadata getStateMachine() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getSimpleName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StateMetadata onTransition(TransitionMetadata transition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isInitial() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isFinal() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public TransitionMetadata[] getPossibleTransitions() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TransitionMetadata getTransition(Object transitionKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isTransitionValid(Object transitionKey) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isOverriding() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public StateMetadata getSuperStateMetadata() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasInboundWhiles() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public RelationMetadata[] getInboundWhiles() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasValidWhiles() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public RelationMetadata[] getValidWhiles() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isCompositeState() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public StateMetadata getOwningState() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StateMachineMetadata getCompositeStateMachine() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StateMetadata getLinkTo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StateMetaBuilder build(Class<?> clazz, StateMachineMetaBuilder parent) {
        return this;
    }
}
