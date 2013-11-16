package net.madz.lifecycle.syntax.basic.transition;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.LifecycleRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.StateMachineBuilder;
import net.madz.lifecycle.SyntaxErrors;
import net.madz.verification.VerificationException;

import org.junit.Test;

public class TransitionSyntaxNegativeTest extends TransitionSyntaxMetadata {

    @Test(expected = VerificationException.class)
    public final void test_transition_conditional_condition_not_match_judger() throws VerificationException {
        @LifecycleRegistry(S1.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {
                super();
            }
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(), SyntaxErrors.TRANSITION_CONDITIONAL_CONDITION_NOT_MATCH_JUDGER,
                    TransitionSyntaxMetadata.S1.Transitions.S1_Transition_X.class, TransitionSyntaxMetadata.S1.Conditions.S1_Condition_B.class,
                    TransitionSyntaxMetadata.S1.VolumeMeasurableTransition.class);
            throw e;
        }
    }
}
