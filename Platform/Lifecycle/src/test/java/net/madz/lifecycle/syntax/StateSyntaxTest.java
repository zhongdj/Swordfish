package net.madz.lifecycle.syntax;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.LifecycleRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.StateMachineBuilder;
import net.madz.lifecycle.Errors;
import net.madz.lifecycle.syntax.StateSyntaxMetadata.S1.States.A;
import net.madz.verification.VerificationException;

import org.junit.Test;

public class StateSyntaxTest extends StateSyntaxMetadata {

    @Test(expected = VerificationException.class)
    public void test() throws VerificationException {
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
}
