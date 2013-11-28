package net.madz.lifecycle.meta.impl.builder.helper;

import java.lang.reflect.Method;

import net.madz.lifecycle.SyntaxErrors;
import net.madz.lifecycle.annotations.callback.AnyState;
import net.madz.lifecycle.annotations.callback.CallbackConsts;
import net.madz.lifecycle.annotations.callback.Callbacks;
import net.madz.lifecycle.annotations.callback.PostStateChange;
import net.madz.lifecycle.annotations.callback.PreStateChange;
import net.madz.lifecycle.meta.impl.builder.StateMachineObjectBuilderImpl;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.lifecycle.meta.template.TransitionMetadata;
import net.madz.util.MethodScanCallback;
import net.madz.verification.VerificationFailureSet;

public final class CallbackMethodVerificationScanner implements MethodScanCallback {

    private final StateMachineObjectBuilderImpl<?> stateMachineObjectBuilderImpl;
    private VerificationFailureSet failureSet;

    public CallbackMethodVerificationScanner(StateMachineObjectBuilderImpl<?> stateMachineObjectBuilderImpl, final VerificationFailureSet failureSet) {
        this.stateMachineObjectBuilderImpl = stateMachineObjectBuilderImpl;
        this.failureSet = failureSet;
    }

    @Override
    public boolean onMethodFound(Method method) {
        final PreStateChange preStateChange = method.getAnnotation(PreStateChange.class);
        if ( null != preStateChange ) {
            verifyPreStateChange(method, failureSet, preStateChange);
        }
        final PostStateChange postStateChange = method.getAnnotation(PostStateChange.class);
        if ( null != postStateChange ) {
            verifyPostStateChange(method, failureSet, postStateChange);
        }
        final Callbacks callbacks = method.getAnnotation(Callbacks.class);
        if ( null != callbacks ) {
            for ( PreStateChange item : callbacks.preStateChange() ) {
                verifyPreStateChange(method, failureSet, item);
            }
            for ( PostStateChange item : callbacks.postStateChange() ) {
                verifyPostStateChange(method, failureSet, item);
            }
        }
        return false;
    }

    private void verifyPostStateChange(Method method, VerificationFailureSet failureSet, final PostStateChange postStateChange) {
        Class<?> fromStateClass = postStateChange.from();
        Class<?> toStateClass = postStateChange.to();
        String relation = postStateChange.observableName();
        if ( CallbackConsts.NULL_STR.equals(relation) ) {
            verifyStateWithoutRelation(method, failureSet, fromStateClass, SyntaxErrors.POST_STATE_CHANGE_FROM_STATE_IS_INVALID);
            verifyStateWithoutRelation(method, failureSet, toStateClass, SyntaxErrors.POST_STATE_CHANGE_TO_STATE_IS_INVALID);
        } else {
            // Relational Syntax verification is done in build stage.
        }
    }

    private void verifyPreStateChange(Method method, VerificationFailureSet failureSet, final PreStateChange preStateChange) {
        Class<?> fromStateClass = preStateChange.from();
        Class<?> toStateClass = preStateChange.to();
        String relation = preStateChange.observableName();
        if ( CallbackConsts.NULL_STR.equals(relation) ) {
            verifyStateWithoutRelation(method, failureSet, fromStateClass, SyntaxErrors.PRE_STATE_CHANGE_FROM_STATE_IS_INVALID);
            verifyStateWithoutRelation(method, failureSet, toStateClass, SyntaxErrors.PRE_STATE_CHANGE_TO_STATE_IS_INVALID);
            if ( AnyState.class != toStateClass && null != ( (StateMachineMetadata) this.stateMachineObjectBuilderImpl.getMetaType() ).getState(toStateClass) ) {
                verifyPreToStatePostEvaluate(method, failureSet, toStateClass, (StateMachineMetadata) this.stateMachineObjectBuilderImpl.getMetaType());
            }
        } else {
            // Relational Syntax verification is done in build stage.
        }
    }

    private void verifyPreToStatePostEvaluate(Method method, VerificationFailureSet failureSet, Class<?> toStateClass, StateMachineMetadata stateMachineMetadata) {
        for ( final TransitionMetadata transition : stateMachineMetadata.getState(toStateClass).getPossibleReachingTransitions() ) {
            if ( transition.isConditional() && transition.postValidate() ) {
                failureSet.add(this.stateMachineObjectBuilderImpl.newVerificationFailure(this.stateMachineObjectBuilderImpl.getDottedPath(),
                        SyntaxErrors.PRE_STATE_CHANGE_TO_POST_EVALUATE_STATE_IS_INVALID, toStateClass, method, transition.getDottedPath()));
            }
        }
    }

    private void verifyStateWithoutRelation(final Method method, final VerificationFailureSet failureSet, final Class<?> stateClass, final String errorCode) {
        if ( AnyState.class != stateClass ) {
            if ( null == ( (StateMachineMetadata) this.stateMachineObjectBuilderImpl.getMetaType() ).getState(stateClass) ) {
                failureSet.add(this.stateMachineObjectBuilderImpl.newVerificationException(method.getDeclaringClass().getName() + "." + stateClass + "."
                        + errorCode, errorCode, stateClass, method, this.stateMachineObjectBuilderImpl.getMetaType().getPrimaryKey()));
            }
        }
    }
}