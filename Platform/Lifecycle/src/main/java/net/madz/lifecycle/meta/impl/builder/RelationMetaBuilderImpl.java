package net.madz.lifecycle.meta.impl.builder;

import java.util.LinkedList;
import java.util.List;

import net.madz.lifecycle.meta.builder.RelationMetaBuilder;
import net.madz.lifecycle.meta.builder.StateMetaBuilder;
import net.madz.lifecycle.meta.instance.ErrorMessageObject;
import net.madz.lifecycle.meta.instance.RelationObject;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.lifecycle.meta.template.StateMetadata;
import net.madz.meta.MetaData;
import net.madz.meta.MetaDataFilter;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailureSet;

public class RelationMetaBuilderImpl extends AnnotationMetaBuilderBase<RelationMetaBuilder, StateMetaBuilder> implements
        RelationMetaBuilder {

    private StateMachineMetadata relatedStateMachine;
    private final LinkedList<StateMetadata> onStates = new LinkedList<>();
    private final LinkedList<ErrorMessageObject> errorMessageObjects = new LinkedList<>();

    @Override
    public StateMachineMetadata getRelatedStateMachine() {
        return relatedStateMachine;
    }

    public RelationMetaBuilderImpl(StateMetaBuilder parent, String name,
            List<StateMetadata> onStates, List<ErrorMessageObject> errorMessageObjects, StateMachineMetadata stateMachineMetadata) {
        super(parent, name);
        this.onStates.addAll(onStates);
        this.errorMessageObjects.addAll(errorMessageObjects);
        this.relatedStateMachine = stateMachineMetadata;
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {
        // TODO Auto-generated method stub
    }

    @Override
    public RelationObject newInstance(Class<?> clazz) {
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
