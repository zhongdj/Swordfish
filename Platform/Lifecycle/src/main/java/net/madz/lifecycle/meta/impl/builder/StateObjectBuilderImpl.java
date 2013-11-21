package net.madz.lifecycle.meta.impl.builder;

import java.util.Arrays;
import java.util.LinkedHashSet;

import net.madz.bcel.intercept.Unlockable;
import net.madz.bcel.intercept.UnlockableStack;
import net.madz.lifecycle.LifecycleCommonErrors;
import net.madz.lifecycle.LifecycleContext;
import net.madz.lifecycle.LifecycleException;
import net.madz.lifecycle.LifecycleLockStrategry;
import net.madz.lifecycle.meta.builder.StateMachineObjectBuilder;
import net.madz.lifecycle.meta.builder.StateObjectBuilder;
import net.madz.lifecycle.meta.instance.StateMachineObject;
import net.madz.lifecycle.meta.instance.StateObject;
import net.madz.lifecycle.meta.template.RelationConstraintMetadata;
import net.madz.lifecycle.meta.template.StateMetadata;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailureSet;

public class StateObjectBuilderImpl<S> extends ObjectBuilderBase<StateObject<S>, StateMachineObject<S>, StateMetadata> implements StateObjectBuilder<S> {

    protected StateObjectBuilderImpl(StateMachineObjectBuilder<S> parent, StateMetadata stateMetadata) {
        super(parent, "StateSet." + stateMetadata.getDottedPath().getName());
        this.setMetaType(stateMetadata);
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {}

    @Override
    public StateObjectBuilder<S> build(Class<?> klass, StateMachineObject<S> parent) throws VerificationException {
        super.build(klass, parent);
        return this;
    }

    @Override
    public void verifyValidWhile(Object target, RelationConstraintMetadata[] relationMetadataArray, final Object relatedTarget, UnlockableStack stack) {
        try {
            final StateMachineObject<?> relatedStateMachineObject = this.getRegistry().loadStateMachineObject(relatedTarget.getClass());
            lockRelatedObject(relatedTarget, stack, relatedStateMachineObject);
            final String relatedStateName = relatedStateMachineObject.evaluateState(relatedTarget);
            boolean found = false;
            for ( RelationConstraintMetadata relationMetadata : relationMetadataArray ) {
                for ( StateMetadata stateMetadata : relationMetadata.getOnStates() ) {
                    if ( stateMetadata.getKeySet().contains(relatedStateName) ) {
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
                throw new LifecycleException(getClass(), LifecycleCommonErrors.BUNDLE, LifecycleCommonErrors.STATE_INVALID, target, this.getMetaType()
                        .getSimpleName(), relatedTarget, relatedStateName, Arrays.toString(validRelationStates.toArray(new String[0])));
            } else {
                relatedStateMachineObject.validateValidWhiles(relatedTarget, stack);
            }
        } catch (VerificationException e) {
            throw new IllegalStateException("Cannot happen, it should be defect of syntax verification.");
        }
    }

    private void lockRelatedObject(final Object relatedTarget, UnlockableStack stack, final StateMachineObject<?> relatedStateMachineObject) {
        if ( !relatedStateMachineObject.isLockEnabled() ) {
            return;
        }
        final LifecycleLockStrategry lifecycleLockStrategy = relatedStateMachineObject.getLifecycleLockStrategy();
        lifecycleLockStrategy.lockRead(relatedTarget);
        stack.pushUnlockable(new Unlockable() {

            @Override
            public void unlock() {
                lifecycleLockStrategy.unlockRead(relatedTarget);
            }
        });
    }

    @Override
    public void verifyInboundWhile(Object transitionKey, Object target, String nextState, RelationConstraintMetadata[] relationMetadataArray,
            Object relatedTarget, UnlockableStack stack) {
        try {
            final StateMachineObject<?> relatedStateMachineObject = this.getRegistry().loadStateMachineObject(relatedTarget.getClass());
            lockRelatedObject(relatedTarget, stack, relatedStateMachineObject);
            final String relatedEvaluateState = relatedStateMachineObject.evaluateState(relatedTarget);
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
            } else {
                relatedStateMachineObject.validateValidWhiles(relatedTarget, stack);
            }
        } catch (VerificationException e) {
            throw new IllegalStateException("Cannot happen, it should be defect of syntax verification.");
        }
    }

    @Override
    public void invokeFromPreStateChangeCallbacks(LifecycleContext<?, S> callbackContext) {}

    @Override
    public void invokeToPreStateChangeCallbacks(LifecycleContext<?, S> callbackContext) {}

    @Override
    public void invokeFromPostStateChangeCallbacks(LifecycleContext<?, S> callbackContext) {}

    @Override
    public void invokeToPostStateChangeCallbacks(LifecycleContext<?, S> callbackContext) {}
}
