package net.madz.lifecycle.meta.impl.builder;

import java.util.LinkedList;
import java.util.List;

import net.madz.common.Dumper;
import net.madz.lifecycle.meta.builder.RelationConstraintBuilder;
import net.madz.lifecycle.meta.builder.StateMetaBuilder;
import net.madz.lifecycle.meta.instance.ErrorMessageObject;
import net.madz.lifecycle.meta.template.RelationConstraintMetadata;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.lifecycle.meta.template.StateMetadata;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailureSet;

public class RelationConstraintBuilderImpl extends InheritableAnnotationMetaBuilderBase<RelationConstraintMetadata, StateMetadata> implements
        RelationConstraintBuilder {

    private StateMachineMetadata relatedStateMachine;
    private final LinkedList<StateMetadata> onStates = new LinkedList<>();
    private final LinkedList<ErrorMessageObject> errorMessageObjects = new LinkedList<>();

    @Override
    public StateMachineMetadata getRelatedStateMachine() {
        return relatedStateMachine;
    }

    public RelationConstraintBuilderImpl(StateMetaBuilder parent, String name, List<StateMetadata> onStates, List<ErrorMessageObject> errorMessageObjects,
            StateMachineMetadata stateMachineMetadata) {
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
    public RelationConstraintBuilder build(Class<?> klass, StateMetadata parent) throws VerificationException {
        super.build(klass, parent);
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

    @Override
    public void dump(Dumper dumper) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void verifySuper(Class<?> metaClass) throws VerificationException {}

    @Override
    protected RelationConstraintMetadata findSuper(Class<?> metaClass) throws VerificationException {
        return null;
    }

    @Override
    protected boolean extendsSuperKeySet() {
        return true;
    }
}
