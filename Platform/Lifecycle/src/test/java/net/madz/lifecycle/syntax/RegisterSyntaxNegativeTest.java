package net.madz.lifecycle.syntax;

import java.util.Iterator;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.LifecycleRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.StateMachineBuilder;
import net.madz.lifecycle.Errors;
import net.madz.lifecycle.meta.impl.builder.StateMachineMetaBuilderImpl;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailure;
import net.madz.verification.VerificationFailureSet;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RegisterSyntaxNegativeTest extends RegisterSyntaxTestMetaData {

    @Test(expected = VerificationException.class)
    public void test_incorrect_registering_without_StateMachine_or_LifecycleMeta() throws VerificationException {
        @LifecycleRegistry(WithoutMetadataAnnotationErrorSyntax.class)
        @StateMachineBuilder(StateMachineMetaBuilderImpl.class)
        class IncorrectStateMachineRegistry extends AbsStateMachineRegistry {

            protected IncorrectStateMachineRegistry() throws VerificationException {
                super();
            }
        }
        try {
            new IncorrectStateMachineRegistry();
        } catch (VerificationException ex) {
            VerificationFailureSet failureSet = ex.getVerificationFailureSet();
            Iterator<VerificationFailure> iterator = failureSet.iterator();
            assertEquals(1, failureSet.size());
            VerificationFailure failure = iterator.next();
            final String expectedErrorMessage = getMessage(Errors.REGISTERED_META_ERROR, new String[] { WithoutMetadataAnnotationErrorSyntax.class.getName() });
            final String actualErrorMessage = failure.getErrorMessage(null);
            assertEquals(expectedErrorMessage, actualErrorMessage);
            assertEquals(Errors.REGISTERED_META_ERROR, failure.getErrorCode());
            throw ex;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_incorrect_registering_superclass_without_StateMachine() throws VerificationException {
        @LifecycleRegistry(IncorrectStateMachineInheritanceChildSyntax.class)
        @StateMachineBuilder(StateMachineMetaBuilderImpl.class)
        class IncorrectStateMachineInheritanceRegistry extends AbsStateMachineRegistry {

            protected IncorrectStateMachineInheritanceRegistry() throws VerificationException {
                super();
            }
        }
        try {
            new IncorrectStateMachineInheritanceRegistry();
        } catch (VerificationException ex) {
            VerificationFailureSet failureSet = ex.getVerificationFailureSet();
            Iterator<VerificationFailure> iterator = failureSet.iterator();
            assertEquals(1, failureSet.size());
            VerificationFailure failure = iterator.next();
            final String expectedErrorMessage = getMessage(Errors.STATEMACHINE_SUPER_MUST_BE_STATEMACHINE,
                    new String[] { IncorrectStateMachineInheritanceSuperSyntax.class.getName() });
            final String actualErrorMessage = failure.getErrorMessage(null);
            assertEquals(expectedErrorMessage, actualErrorMessage);
            assertEquals(Errors.STATEMACHINE_SUPER_MUST_BE_STATEMACHINE, failure.getErrorCode());
            throw ex;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_incorrect_registering_with_multi_super_interfaces() throws VerificationException {
        @LifecycleRegistry(IncorrectStateMachineInheritanceChildWithMultiSuperInterfacesSyntax.class)
        @StateMachineBuilder(StateMachineMetaBuilderImpl.class)
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
            VerificationFailure failure = iterator.next();
            final String expectedErrorMessage = getMessage(Errors.STATEMACHINE_HAS_ONLY_ONE_SUPER_INTERFACE,
                    new String[] { IncorrectStateMachineInheritanceChildWithMultiSuperInterfacesSyntax.class.getName() });
            final String actualErrorMessage = failure.getErrorMessage(null);
            assertEquals(expectedErrorMessage, actualErrorMessage);
            assertEquals(Errors.STATEMACHINE_HAS_ONLY_ONE_SUPER_INTERFACE, failure.getErrorCode());
            throw ex;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_incorrect_registering_lifecycleMeta_value_without_stateMachine_annotation() throws VerificationException {
        @LifecycleRegistry(value = { WrongLifecycleMetaSyntaxWithStateMachineWithoutAnnotation.class })
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {
            }
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(), Errors.STATEMACHINE_CLASS_WITHOUT_ANNOTATION,
                    WrongStateMachineSyntaxWithoutAnnotation.class.getName());
            throw e;
        }
    }
}
