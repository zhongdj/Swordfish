package net.madz.lifecycle.syntax;

import java.util.Iterator;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.LifecycleRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.StateMachineMetadataBuilder;
import net.madz.lifecycle.Errors;
import net.madz.lifecycle.meta.builder.StateMachineMetaBuilder;
import net.madz.lifecycle.meta.impl.builder.StateMachineMetaBuilderImpl;
import net.madz.utils.BundleUtils;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailure;
import net.madz.verification.VerificationFailureSet;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RegisterSyntaxTest extends RegisterSyntaxTestMetaData {

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

    @Test
    public void test_incorrect_registering_without_StateMachine_or_LifecycleMeta() {
        @LifecycleRegistry(WithoutMetadataAnnotationErrorSyntax.class)
        @StateMachineMetadataBuilder(StateMachineMetaBuilderImpl.class)
        class IncorrectStateMachineRegistry extends AbsStateMachineRegistry {

            protected IncorrectStateMachineRegistry() throws VerificationException {
                super();
            }
        }
        try {
            new IncorrectStateMachineRegistry();
            fail("Verification Exception expected.");
        } catch (VerificationException ex) {
            VerificationFailureSet failureSet = ex.getVerificationFailureSet();
            Iterator<VerificationFailure> iterator = failureSet.iterator();
            assertEquals(1, failureSet.size());
            while ( iterator.hasNext() ) {
                VerificationFailure failure = iterator.next();
                final String expectedErrorMessage = BundleUtils.getBundledMessage(StateMachineMetaBuilder.class,
                        "syntax_error", Errors.REGISTERED_META_ERROR,
                        new String[] { WithoutMetadataAnnotationErrorSyntax.class.getName() });
                final String actualErrorMessage = failure.getErrorMessage(null);
                assertEquals(expectedErrorMessage, actualErrorMessage);
                assertEquals(Errors.REGISTERED_META_ERROR, failure.getErrorCode());
            }
        }
    }

    @Test
    public void test_incorrect_registering_superclass_without_StateMachine() {
        @LifecycleRegistry(IncorrectStateMachineInheritanceChildSyntax.class)
        @StateMachineMetadataBuilder(StateMachineMetaBuilderImpl.class)
        class IncorrectStateMachineInheritanceRegistry extends AbsStateMachineRegistry {

            protected IncorrectStateMachineInheritanceRegistry() throws VerificationException {
                super();
            }
        }
        try {
            new IncorrectStateMachineInheritanceRegistry();
            fail("Verification Exception expected.");
        } catch (VerificationException ex) {
            VerificationFailureSet failureSet = ex.getVerificationFailureSet();
            Iterator<VerificationFailure> iterator = failureSet.iterator();
            assertEquals(1, failureSet.size());
            while ( iterator.hasNext() ) {
                VerificationFailure failure = iterator.next();
                final String expectedErrorMessage = BundleUtils.getBundledMessage(StateMachineMetaBuilder.class,
                        "syntax_error", Errors.STATEMACHINE_SUPER_MUST_BE_STATEMACHINE,
                        new String[] { IncorrectStateMachineInheritanceSuperSyntax.class.getName() });
                final String actualErrorMessage = failure.getErrorMessage(null);
                assertEquals(expectedErrorMessage, actualErrorMessage);
                assertEquals(Errors.STATEMACHINE_SUPER_MUST_BE_STATEMACHINE, failure.getErrorCode());
            }
        }
    }

    @Test
    public void test_incorrect_registering_with_multi_super_interfaces() {
        @LifecycleRegistry(IncorrectStateMachineInheritanceChildWithMultiSuperInterfacesSyntax.class)
        @StateMachineMetadataBuilder(StateMachineMetaBuilderImpl.class)
        class IncorrectStateMachineInheritanceWithMultiSuperInterfacesRegistry extends AbsStateMachineRegistry {

            protected IncorrectStateMachineInheritanceWithMultiSuperInterfacesRegistry() throws VerificationException {
                super();
            }
        }
        try {
            new IncorrectStateMachineInheritanceWithMultiSuperInterfacesRegistry();
        } catch (VerificationException ex) {
            VerificationFailureSet actualFailureSet = ex.getVerificationFailureSet();
            assertEquals(1, actualFailureSet.size());
            Iterator<VerificationFailure> iterator = actualFailureSet.iterator();
            while ( iterator.hasNext() ) {
                VerificationFailure failure = iterator.next();
                final String expectedErrorMessage = BundleUtils.getBundledMessage(StateMachineMetaBuilder.class,
                        Errors.SYNTAX_ERROR, Errors.STATEMACHINE_HAS_ONLY_ONE_SUPER_INTERFACE,
                        new String[] { IncorrectStateMachineInheritanceChildWithMultiSuperInterfacesSyntax.class
                                .getName() });
                final String actualErrorMessage = failure.getErrorMessage(null);
                assertEquals(expectedErrorMessage, actualErrorMessage);
                assertEquals(Errors.STATEMACHINE_HAS_ONLY_ONE_SUPER_INTERFACE, failure.getErrorCode());
            }
        }
    }
}
