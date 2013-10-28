package net.madz.lifecycle.meta.impl.builder;

import net.madz.common.Dumper;
import net.madz.lifecycle.meta.builder.StateMachineMetaBuilder;
import net.madz.lifecycle.meta.impl.StateMachineInstImpl;
import net.madz.lifecycle.meta.instance.StateMachineInst;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.lifecycle.meta.template.StateMetadata;
import net.madz.lifecycle.meta.template.TransitionMetadata;
import net.madz.meta.MetaData;
import net.madz.meta.MetaDataFilter;
import net.madz.meta.MetaDataFilterable;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailureSet;

public class StateMachineMetaBuilderImpl extends
        AnnotationBasedMetaBuilder<StateMachineMetadata, StateMachineMetadata> implements StateMachineMetaBuilder {

    public StateMachineMetaBuilderImpl(StateMachineMetadata parent, String name) {
        super(parent, name);
    }

    public StateMachineMetaBuilderImpl(String name) {
        this(null, name);
    }

    @Override
    public boolean hasSuper() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public StateMachineMetadata getSuperStateMachine() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasParent() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean hasRelations() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public StateMachineMetadata[] getRelatedStateMachineMetadata() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StateMetadata[] getStateSet() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StateMetadata getState(Object stateKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StateMetadata getInitialState() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StateMetadata[] getFinalStates() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TransitionMetadata[] getTransitionSet() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TransitionMetadata getTransition(Object transitionKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TransitionMetadata getStateSynchronizationTransition() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isComposite() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public StateMachineMetadata getOwningStateMachine() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StateMetadata getCompositeState() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StateMetadata[] getShortcutStateSet() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StateMachineInst newInstance(Class<?> clazz) {
        return new StateMachineInstBuilderImpl(this, clazz.getSimpleName()).build(clazz).getMetaData();
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {
        // TODO Auto-generated method stub
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
    public StateMachineMetaBuilder build(Class<?> clazz) throws VerificationException {
        return this;
    }
}
