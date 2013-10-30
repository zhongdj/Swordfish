package net.madz.lifecycle.syntax;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.LifecycleRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.StateMachineBuilder;
import net.madz.lifecycle.Errors;
import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.S1.States.A;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.S2.States.C;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.S3.States.E;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.S3.Transitions.Y;
import net.madz.verification.VerificationException;

import org.junit.Test;

public class StateSyntaxNegativeTest extends StateSyntaxMetadata {

    @Test(expected = VerificationException.class)
    public void test_non_final_state_without_functions() throws VerificationException {
        @LifecycleRegistry(S1.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(), Errors.STATE_NON_FINAL_WITHOUT_FUNCTIONS,
                    A.class.getName());
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_state_function__with_invalid_transition() throws VerificationException {
        @LifecycleRegistry(S2.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(), Errors.INVALID_TRANSITION_REFERENCE,
                    C.class.getAnnotation(Function.class), C.class.getName(),
                    net.madz.lifecycle.syntax.StateSyntaxMetadata.S1.Transitions.X.class.getName());
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_state_function_with_invalid_conditional_transition_without_conditional_annotation()
            throws VerificationException {
        @LifecycleRegistry(S3.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    Errors.CONDITIONAL_TRANSITION_WITHOUT_CONDITION, E.class.getAnnotation(Function.class),
                    E.class.getName(), Y.class);
            throw e;
        }
    }
}
