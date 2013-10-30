package net.madz.lifecycle.syntax;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.LifecycleRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.StateMachineBuilder;
import net.madz.verification.VerificationException;

import org.junit.Test;

public class StateSyntaxPositiveTest extends StateSyntaxMetadata {

    @Test
    public void test_state_function_with_valid_conditional_transition() throws VerificationException {
        @LifecycleRegistry(S4.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        new Registry();
    }

    @Test
    public void test_state_function_with_valid_next_state_set() throws VerificationException {
        @LifecycleRegistry(S7.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        new Registry();
    }

    @Test
    public void test_composite_state_with_valid_transition_and_shortcut() throws VerificationException {
        @LifecycleRegistry(PCS1.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        new Registry();
    }
}
