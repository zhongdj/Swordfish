package net.madz.lifecycle.meta.impl.builder;

import java.util.Arrays;
import java.util.LinkedHashSet;

import net.madz.lifecycle.LifecycleCommonErrors;
import net.madz.lifecycle.LifecycleException;
import net.madz.lifecycle.meta.builder.StateMachineObjectBuilder;
import net.madz.lifecycle.meta.builder.StateObjectBuilder;
import net.madz.lifecycle.meta.instance.StateMachineObject;
import net.madz.lifecycle.meta.instance.StateMachineObject.ReadAccessor;
import net.madz.lifecycle.meta.instance.StateObject;
import net.madz.lifecycle.meta.template.RelationConstraintMetadata;
import net.madz.lifecycle.meta.template.StateMetadata;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailureSet;

public class StateObjectBuilderImpl extends ObjectBuilderBase<StateObject, StateMachineObject> implements StateObjectBuilder {

    private StateMetadata template;

    protected StateObjectBuilderImpl(StateMachineObjectBuilder parent, StateMetadata stateMetadata) {
        super(parent, "StateSet." + stateMetadata.getDottedPath().getName());
        this.template = stateMetadata;
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {}

    @Override
    public StateObjectBuilder build(Class<?> klass, StateMachineObject parent) throws VerificationException {
        addKeys(template.getKeySet());
        return this;
    }

    @Override
    public StateMetadata getMetaType() {
        return template;
    }

    @Override
    public void verifyValidWhile(Object target, RelationConstraintMetadata[] relationMetadataArray, ReadAccessor<?> evaluator) {
        try {
            final Object relatedTarget = evaluator.read(target);
            final StateMachineObject relatedStateMachineObject = this.getRegistry().loadStateMachineObject(relatedTarget.getClass());
            final String relatedEvaluateState = relatedStateMachineObject.evaluateState(relatedTarget);
            boolean found = false;
            for ( RelationConstraintMetadata relationMetadata : relationMetadataArray ) {
                for ( StateMetadata stateMetadata : relationMetadata.getOnStates() ) {
                    if ( stateMetadata.getKeySet().contains(relatedEvaluateState) ) {
                        found = true;
                        break;
                    }
                }
            }
            if ( !found ) {
                final LinkedHashSet<String> validRelationStates = new LinkedHashSet<>();
                for ( RelationConstraintMetadata relationMetadata : relationMetadataArray ) {
                    for ( StateMetadata metadata : relationMetadata.getOnStates() ) {
                        validRelationStates.add(metadata.getSimpleName());
                    }
                }
                throw new LifecycleException(getClass(), LifecycleCommonErrors.BUNDLE, LifecycleCommonErrors.STATE_INVALID, target,
                        this.template.getSimpleName(), relatedTarget, relatedEvaluateState, Arrays.toString(validRelationStates.toArray(new String[0])));
            }
        } catch (VerificationException e) {
            throw new IllegalStateException("Cannot happen, it should be defect of syntax verification.");
        }
    }

    @Override
    public void verifyInboundWhile(Object transitionKey, Object target, String nextState, RelationConstraintMetadata[] relationMetadataArray,
            ReadAccessor<?> evaluator) {
        try {
            final Object relatedTarget = evaluator.read(target);
            StateMachineObject relatedStateMachineInst;
            relatedStateMachineInst = this.getRegistry().loadStateMachineObject(relatedTarget.getClass());
            final String relatedEvaluateState = relatedStateMachineInst.evaluateState(relatedTarget);
            boolean find = false;
            for ( RelationConstraintMetadata relationMetadata : relationMetadataArray ) {
                for ( StateMetadata stateMetadata : relationMetadata.getOnStates() ) {
                    if ( stateMetadata.getKeySet().contains(relatedEvaluateState) ) {
                        find = true;
                        break;
                    }
                }
            }
            if ( !find ) {
                final LinkedHashSet<String> validRelationStates = new LinkedHashSet<>();
                for ( RelationConstraintMetadata relationMetadata : relationMetadataArray ) {
                    for ( StateMetadata metadata : relationMetadata.getOnStates() ) {
                        validRelationStates.add(metadata.getSimpleName());
                    }
                }
                throw new LifecycleException(getClass(), LifecycleCommonErrors.BUNDLE, LifecycleCommonErrors.VIOLATE_INBOUND_WHILE_RELATION_CONSTRAINT,
                        transitionKey, nextState, target, relatedTarget, relatedEvaluateState, Arrays.toString(validRelationStates.toArray(new String[0])));
            }
        } catch (VerificationException e) {
            throw new IllegalStateException("Cannot happen, it should be defect of syntax verification.");
        }
    }
}
