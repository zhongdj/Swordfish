package net.madz.lifecycle.syntax;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.LifecycleRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.StateMachineBuilder;
import net.madz.lifecycle.Errors;
import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.state.ShortCut;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.NCS2.States.NCS2_B.CStates.NCS2_CC;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.NCS3.States.NCS3_B.CStates.NCS3_CC;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.NCS4.States.NCS4_B.CStates.NCS4_CC;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.NSC1.States.NSC1_B.CStates.NSC1_CB;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.NSC1.States.NSC1_C;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.NSC1.Transitions.NSC1_X;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.S1.States.A;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.S2.States.C;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.S2.States.D;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.S3.States.E;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.S3.Transitions.Y;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.S5.states.S5_A;
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
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    Errors.FUNCTION_INVALID_TRANSITION_REFERENCE, C.class.getAnnotation(Function.class),
                    C.class.getName(), net.madz.lifecycle.syntax.StateSyntaxMetadata.S1.Transitions.X.class.getName());
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
                    Errors.FUNCTION_CONDITIONAL_TRANSITION_WITHOUT_CONDITION, E.class.getAnnotation(Function.class),
                    E.class.getName(), Y.class.getName());
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_state_function_with_invalid_next_state() throws VerificationException {
        @LifecycleRegistry(S5.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    Errors.FUNCTION_NEXT_STATESET_OF_FUNCTION_INVALID, S5_A.class.getAnnotation(Function.class),
                    S5_A.class.getName(), S5.class.getName(), D.class.getName());
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_composite_state_with_reference_transition_beyond_scope() throws VerificationException {
        @LifecycleRegistry(NSC1.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    Errors.FUNCTION_TRANSITION_REFERENCE_BEYOND_COMPOSITE_STATE_SCOPE,
                    NSC1_CB.class.getAnnotation(Function.class), NSC1_CB.class.getName(), NSC1_X.class.getName());
            throw e;
        }
    }
    @Test(expected = VerificationException.class)
    public void test_shortcut_referencing_state_beyond_scope() throws VerificationException {
        @LifecycleRegistry(NCS2.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {
            
            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    Errors.COMPOSITE_STATEMACHINE_SHORTCUT_STATE_INVALID,
                    NCS2_CC.class.getAnnotation(ShortCut.class), NCS2_CC.class, NSC1_C.class);
            throw e;
        }
    }
    @Test(expected = VerificationException.class)
    public void test_composite_final_without_shortcut() throws VerificationException {
        @LifecycleRegistry(NCS3.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {
            
            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    Errors.COMPOSITE_STATEMACHINE_FINAL_STATE_WITHOUT_SHORTCUT,
                    NCS3_CC.class);
            throw e;
        }
    }
    @Test(expected = VerificationException.class)
    public void test_shortcut_without_end_annotation() throws VerificationException {
        @LifecycleRegistry(NCS4.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {
            
            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    Errors.COMPOSITE_STATEMACHINE_SHORTCUT_WITHOUT_END,
                    NCS4_CC.class);
            throw e;
        }
    }
}
