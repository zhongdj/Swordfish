package net.madz.lifecycle.syntax;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.LifecycleRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.StateMachineMetadataBuilder;
import net.madz.lifecycle.meta.impl.builder.StateMachineMetaBuilderImpl;
import net.madz.verification.VerificationException;

import org.junit.Test;

public class RegisterSyntaxPositiveTest extends RegisterSyntaxTestMetaData {

    @Test
    public void test_correct_metadata_without_syntax_error() throws VerificationException {
        @LifecycleRegistry({ CorrectStateMachineSyntax.class, CorrectLifecycleMetaSyntax.class })
        @StateMachineMetadataBuilder(StateMachineMetaBuilderImpl.class)
        class CorrectRegistry extends AbsStateMachineRegistry {

            protected CorrectRegistry() throws VerificationException {
                super();
            }
        }
        new CorrectRegistry();
    }

    @Test
    public void test_correct_inheritance_statemachine() throws VerificationException {
        @LifecycleRegistry({ CorrectStateMachineInheritanceChildSyntax.class })
        @StateMachineMetadataBuilder(StateMachineMetaBuilderImpl.class)
        class CorrectInheritanceRegistry extends AbsStateMachineRegistry {

            protected CorrectInheritanceRegistry() throws VerificationException {
                super();
            }
        }
        new CorrectInheritanceRegistry();
    }
}
