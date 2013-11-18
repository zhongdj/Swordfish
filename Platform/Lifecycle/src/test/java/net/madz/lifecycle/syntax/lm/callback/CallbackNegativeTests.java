package net.madz.lifecycle.syntax.lm.callback;

import net.madz.bcel.intercept.LifecycleInterceptor;
import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.LifecycleRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.StateMachineBuilder;
import net.madz.lifecycle.SyntaxErrors;
import net.madz.lifecycle.syntax.lm.callback.CallbackTestBase.S1.States.S1_State_C;
import net.madz.verification.VerificationException;

import org.junit.Test;

public class CallbackNegativeTests extends CallbackTestBase {

    
    @Test(expected = VerificationException.class)
    public final void test_PreStateChange_To_Possible_Next_State() throws NoSuchMethodException, SecurityException, VerificationException {
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
            assertFailure(e.getVerificationFailureSet().iterator().next(), SyntaxErrors.PRE_STATE_CHANGE_TO_POST_EVALUATE_STATE_IS_INVALID, S1_State_C.class, NLM_With_PreStateChange_To_Possible_Next_State.class.getMethod("interceptStateChange", LifecycleInterceptor.class));
            throw e;
        }
    }
}
