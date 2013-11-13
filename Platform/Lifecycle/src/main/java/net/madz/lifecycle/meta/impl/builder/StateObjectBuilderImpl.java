package net.madz.lifecycle.meta.impl.builder;

import java.util.Arrays;
import java.util.LinkedHashSet;

import net.madz.lifecycle.LifecycleCommonErrors;
import net.madz.lifecycle.LifecycleException;
import net.madz.lifecycle.meta.builder.StateMachineObjectBuilder;
import net.madz.lifecycle.meta.builder.StateObjectBuilder;
import net.madz.lifecycle.meta.instance.StateMachineObject;
import net.madz.lifecycle.meta.instance.StateMachineObject.ReadAccessor;
import net.madz.lifecycle.meta.template.RelationMetadata;
import net.madz.lifecycle.meta.template.StateMetadata;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailureSet;

public class StateObjectBuilderImpl extends ObjectBuilderBase<StateObjectBuilder, StateMachineObjectBuilder> implements
        StateObjectBuilder {

    private StateMetadata template;

    protected StateObjectBuilderImpl(StateMachineObjectBuilder parent, StateMetadata stateMetadata) {
        super(parent, "StateSet." + stateMetadata.getDottedPath().getName());
        this.template = stateMetadata;
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {}

    @Override
    public StateObjectBuilder build(Class<?> klass, StateMachineObjectBuilder parent) throws VerificationException {
        addKeys(template.getKeySet());
        return this;
    }

    @Override
    public StateMetadata getTemplate() {
        return template;
    }

    @Override
    public void verifyValidWhile(Object target, RelationMetadata[] relationMetadataArray, ReadAccessor<?> evaluator) {
        final Object relatedTarget = evaluator.read(target);
        final StateMachineObject relatedStateMachineInst = this.getRegistry().getStateMachineInst(
                relatedTarget.getClass().getName());
        final String relatedEvaluateState = relatedStateMachineInst.evaluateState(relatedTarget);
        boolean find = false;
        for ( RelationMetadata relationMetadata : relationMetadataArray ) {
            for ( StateMetadata stateMetadata : relationMetadata.getOnStates() ) {
                if ( stateMetadata.getKeySet().contains(relatedEvaluateState) ) {
                    find = true;
                    break;
                }
            }
        }
        if ( false == find ) {
            final LinkedHashSet<String> validRelationStates = new LinkedHashSet<>();
            for ( RelationMetadata relationMetadata : relationMetadataArray ) {
                for ( StateMetadata metadata : relationMetadata.getOnStates() ) {
                    validRelationStates.add(metadata.getSimpleName());
                }
            }
            throw new LifecycleException(getClass(), LifecycleCommonErrors.BUNDLE, LifecycleCommonErrors.STATE_INVALID,
                    target, this.template.getSimpleName(), relatedTarget, relatedEvaluateState,
                    Arrays.toString(validRelationStates.toArray(new String[0])));
        }
    }
}
