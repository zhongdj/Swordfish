package net.madz.lifecycle.syntax;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.LifecycleRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.StateMachineBuilder;
import net.madz.lifecycle.Errors;
import net.madz.verification.VerificationException;

import org.junit.Test;

public class LMSyntaxNegativeTest extends LMSyntaxMetadata {

    @Test(expected = VerificationException.class)
    public void test_LM_partial_concreting_transitions_A() throws VerificationException {
        @LifecycleRegistry(NLM_1.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {
            }
        }
        
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(), Errors.LM_MUST_CONCRETE_ALL_TRANSITIONS, NLM_1.class.getName());
            throw e;
        }
    }
    
}
