package net.madz.lifecycle.meta.impl.builder;

import java.util.LinkedList;

import net.madz.lifecycle.meta.builder.RelationMetaBuilder;
import net.madz.lifecycle.meta.builder.StateMetaBuilder;
import net.madz.lifecycle.meta.instance.ErrorMessageObject;
import net.madz.lifecycle.meta.instance.RelationInst;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.lifecycle.meta.template.StateMetadata;
import net.madz.meta.MetaData;
import net.madz.meta.MetaDataFilter;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailureSet;

public class RelationMetaBuilderImpl extends AnnotationMetaBuilderBase<RelationMetaBuilder, StateMetaBuilder> implements
        RelationMetaBuilder {

    private StateMachineMetadata relatedStateMachine;
    private final LinkedList<StateMetadata> onStates;
    private final LinkedList<ErrorMessageObject> errorMessageObjects;

    @Override
    public StateMachineMetadata getRelatedStateMachine() {
        return relatedStateMachine;
    }

    public RelationMetaBuilderImpl(StateMetaBuilder parent, Class<?> relationKey,
            LinkedList<StateMetadata> onStates, LinkedList<ErrorMessageObject> errorMessageObjects, StateMachineMetadata stateMachineMetadata) {
        super(parent, "RelationSet." + relationKey.getSimpleName());
        this.onStates = onStates;
        this.errorMessageObjects = errorMessageObjects;
        this.relatedStateMachine = stateMachineMetadata;
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {
        // TODO Auto-generated method stub
    }

    @Override
    public RelationInst newInstance(Class<?> clazz) {
        return null;
    }

    @Override
    public RelationMetaBuilder build(Class<?> klass, StateMetaBuilder parent) throws VerificationException {
        addKeys(klass);
        return this;
    }

    @Override
    public RelationMetaBuilder filter(MetaData parent, MetaDataFilter filter, boolean lazyFilter) {
        return this;
    }

    @Override
    public StateMetadata[] getOnStates() {
        return this.onStates.toArray(new StateMetadata[0]);
    }

    @Override
    public ErrorMessageObject[] getErrorMessageObjects() {
        return this.errorMessageObjects.toArray(new ErrorMessageObject[0]);
    }
}
