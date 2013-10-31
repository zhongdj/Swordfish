package net.madz.lifecycle.syntax;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.LifecycleRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.StateMachineBuilder;
import net.madz.verification.VerificationException;

import org.junit.Test;

public class LMSyntaxPositiveTest extends LMSyntaxMetadata {

    @Test
    public void test_LM_with_correct_SM() throws VerificationException {
        @LifecycleRegistry(PLM_1.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {
            }
        }
        new Registry();
    }
    
    @Test
    public void test_LM_concrete_all_transitions_with_explicit_transition_name () throws VerificationException {
        @LifecycleRegistry(PLM_2.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {
            }
        }
        new Registry();
    }
}
