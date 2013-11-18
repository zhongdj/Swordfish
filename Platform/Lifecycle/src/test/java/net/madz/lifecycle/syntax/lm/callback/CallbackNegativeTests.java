package net.madz.lifecycle.syntax.lm.callback;

import net.madz.bcel.intercept.LifecycleInterceptor;
import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.LifecycleRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.StateMachineBuilder;
import net.madz.lifecycle.SyntaxErrors;
import net.madz.lifecycle.syntax.lm.callback.CallbackTestBase.S2;
import net.madz.lifecycle.syntax.lm.callback.CallbackTestBase.S1.States.S1_State_C;
import net.madz.verification.VerificationException;

import org.junit.Test;

public class CallbackNegativeTests extends CallbackTestBase {

    @Test(expected = VerificationException.class)
    public final void test_prestatechange_to_possible_next_state() throws NoSuchMethodException, SecurityException,
            VerificationException {
        @LifecycleRegistry(NLM_With_PreStateChange_To_Possible_Next_State.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {
                super();
            }
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    SyntaxErrors.PRE_STATE_CHANGE_TO_POST_EVALUATE_STATE_IS_INVALID, S1_State_C.class,
                    NLM_With_PreStateChange_To_Possible_Next_State.class.getMethod("interceptStateChange",
                            LifecycleInterceptor.class));
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_prestatechange_from_state_invalid() throws VerificationException, NoSuchMethodException,
            SecurityException {
        @LifecycleRegistry(NLM_prestatechange_from_state_invalid.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    SyntaxErrors.PRE_STATE_CHANGE_FROM_STATE_IS_INVALID, S2.States.S2_State_A.class,
                    NLM_prestatechange_from_state_invalid.class.getMethod("interceptStateChange",
                            LifecycleInterceptor.class), S1.class);
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_prestatechange_to_state_invalid() throws VerificationException, NoSuchMethodException,
            SecurityException {
        @LifecycleRegistry(NLM_prestatechange_to_state_invalid.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    SyntaxErrors.PRE_STATE_CHANGE_TO_STATE_IS_INVALID, S2.States.S2_State_A.class,
                    NLM_prestatechange_to_state_invalid.class.getMethod("interceptStateChange",
                            LifecycleInterceptor.class), S1.class);
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_poststatechange_from_state_invalid() throws VerificationException, NoSuchMethodException,
            SecurityException {
        @LifecycleRegistry(NLM_poststatechange_from_state_invalid.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    SyntaxErrors.POST_STATE_CHANGE_FROM_STATE_IS_INVALID, S2.States.S2_State_A.class,
                    NLM_poststatechange_from_state_invalid.class.getMethod("interceptStateChange",
                            LifecycleInterceptor.class), S1.class);
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_poststatechange_to_state_invalid() throws VerificationException, NoSuchMethodException,
            SecurityException {
        @LifecycleRegistry(NLM_poststatechange_to_state_invalid.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    SyntaxErrors.POST_STATE_CHANGE_TO_STATE_IS_INVALID, S2.States.S2_State_A.class,
                    NLM_poststatechange_to_state_invalid.class.getMethod("interceptStateChange",
                            LifecycleInterceptor.class), S1.class);
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_prestatechange_relation_invalid() throws VerificationException, NoSuchMethodException,
            SecurityException {
        @LifecycleRegistry(NLM_prestatechange_relation_invalid.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    SyntaxErrors.PRE_STATE_CHANGE_RELATION_INVALID, "s1",
                    NLM_prestatechange_relation_invalid.class.getMethod("interceptStateChange",
                            LifecycleInterceptor.class), NLM_prestatechange_relation_invalid.class);
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_prestatechange_mappedby_invalid() throws VerificationException, NoSuchMethodException,
            SecurityException {
        @LifecycleRegistry(NLM_prestatechange_mappedby_invalid.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    SyntaxErrors.PRE_STATE_CHANGE_MAPPEDBY_INVALID, "s1",
                    NLM_prestatechange_mappedby_invalid.class.getMethod("interceptStateChange",
                            LifecycleInterceptor.class), S1BaseLM.class);
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_poststatechange_relation_invalid() throws VerificationException, NoSuchMethodException,
            SecurityException {
        @LifecycleRegistry(NLM_poststatechange_relation_invalid.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    SyntaxErrors.POST_STATE_CHANGE_RELATION_INVALID, "s1",
                    NLM_poststatechange_relation_invalid.class.getMethod("interceptStateChange",
                            LifecycleInterceptor.class), NLM_poststatechange_relation_invalid.class);
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_poststatechange_mappedby_invalid() throws VerificationException, NoSuchMethodException,
            SecurityException {
        @LifecycleRegistry(NLM_poststatechange_mappedby_invalid.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    SyntaxErrors.POST_STATE_CHANGE_MAPPEDBY_INVALID, "s1",
                    NLM_poststatechange_mappedby_invalid.class.getMethod("interceptStateChange",
                            LifecycleInterceptor.class), S1BaseLM.class);
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_callbacks_with_invalid_states() throws NoSuchMethodException, SecurityException,
            VerificationException {
        @LifecycleRegistry(NLM_With_CallBacksWithInvalidStates.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    SyntaxErrors.PRE_STATE_CHANGE_FROM_STATE_IS_INVALID, S2.States.S2_State_A.class,
                    NLM_With_CallBacksWithInvalidStates.class.getMethod("interceptStates", LifecycleInterceptor.class),
                    S1.class);
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    SyntaxErrors.POST_STATE_CHANGE_TO_STATE_IS_INVALID, S2.States.S2_State_A.class,
                    NLM_With_CallBacksWithInvalidStates.class.getMethod("interceptStates", LifecycleInterceptor.class),
                    S1.class);
            throw e;
        }
    }
}
