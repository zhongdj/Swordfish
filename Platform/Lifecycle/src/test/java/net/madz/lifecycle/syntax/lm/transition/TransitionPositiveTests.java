package net.madz.lifecycle.syntax.lm.transition;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.LifecycleRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.StateMachineBuilder;
import net.madz.verification.VerificationException;

import org.junit.Test;

public class TransitionPositiveTests extends TransitionTestMetadata {

    @Test
    public void test_special_transition_type() throws VerificationException {
        @LifecycleRegistry(PositiveProcess.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        new Registry();
    }
}
