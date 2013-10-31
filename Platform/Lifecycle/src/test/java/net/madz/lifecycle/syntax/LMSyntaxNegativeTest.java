package net.madz.lifecycle.syntax;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.LifecycleRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.StateMachineBuilder;
import net.madz.lifecycle.Errors;
import net.madz.lifecycle.syntax.LMSyntaxMetadata.S2.Transitions.NS1_Z;
import net.madz.verification.VerificationException;

import org.junit.Test;

public class LMSyntaxNegativeTest extends LMSyntaxMetadata {

    @Test(expected = VerificationException.class)
    public void test_LM_partial_concreting_transitions_A() throws VerificationException {
        @LifecycleRegistry(NLM_1.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(), Errors.LM_TRANSITION_NOT_CONCRETED_IN_LM,
                    NLM_1.class.getSimpleName(),NS1_Z.class.getSimpleName(), S2.class.getName());
            throw e;
        } 
    }
    
    @Test(expected = VerificationException.class)
    public void test_LM_partial_concreting_transitions_B() throws VerificationException {
        @LifecycleRegistry(NLM_2.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(), Errors.LM_TRANSITION_NOT_CONCRETED_IN_LM,
                    NLM_2.class.getSimpleName(),NS1_Z.class.getSimpleName(), S2.class.getName());
            throw e;
        } 
    }
}
