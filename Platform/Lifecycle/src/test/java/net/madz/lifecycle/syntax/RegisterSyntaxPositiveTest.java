package net.madz.lifecycle.syntax;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.LifecycleRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.StateMachineMetadataBuilder;
import net.madz.lifecycle.meta.impl.builder.StateMachineMetaBuilderImpl;
import net.madz.verification.VerificationException;

import org.junit.Test;

import static org.junit.Assert.fail;


public class RegisterSyntaxPositiveTest extends RegisterSyntaxTestMetaData{

    @Test
    public void test_correct_metadata_without_syntax_error() {
        @LifecycleRegistry({ CorrectStateMachineSyntax.class, CorrectLifecycleMetaSyntax.class })
        @StateMachineMetadataBuilder(StateMachineMetaBuilderImpl.class)
        class CorrectRegistry extends AbsStateMachineRegistry {

            protected CorrectRegistry() throws VerificationException {
                super();
            }
        }
        try {
            new CorrectRegistry();
        } catch (VerificationException e) {
            fail("No Exception expected");
        }
    }

}