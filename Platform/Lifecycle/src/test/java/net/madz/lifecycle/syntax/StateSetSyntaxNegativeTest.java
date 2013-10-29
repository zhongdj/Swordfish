package net.madz.lifecycle.syntax;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.LifecycleRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.StateMachineMetadataBuilder;
import net.madz.lifecycle.Errors;
import net.madz.lifecycle.meta.impl.builder.StateMachineMetaBuilderImpl;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailure;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StateSetSyntaxNegativeTest extends StateSetSyntaxMetadata {

    @Test(expected = VerificationException.class)
    public void test_StateMachine_without_InnerClasses() throws VerificationException {
        @LifecycleRegistry(Negative_No_InnerClasses.class)
        @StateMachineMetadataBuilder(StateMachineMetaBuilderImpl.class)
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertEquals(1, e.getVerificationFailureSet().size());
            final VerificationFailure failure = e.getVerificationFailureSet().iterator().next();
            assertEquals(Errors.STATEMACHINE_WITHOUT_INNER_CLASSES_OR_INTERFACES, failure.getErrorCode());
            final String expectedMessage = getMessage(Errors.STATEMACHINE_WITHOUT_INNER_CLASSES_OR_INTERFACES,
                    new Object[] { Negative_No_InnerClasses.class.getName() });
            assertEquals(expectedMessage, failure.getErrorMessage(null));
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_StateMachine_without_StateSet_and_TransitionSet() throws VerificationException {
        @LifecycleRegistry(Negative_No_StateSet_Aand_TransitionSet.class)
        @StateMachineMetadataBuilder(StateMachineMetaBuilderImpl.class)
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertEquals(2, e.getVerificationFailureSet().size());
            final VerificationFailure failureOne = e.getVerificationFailureSet().iterator().next();
            final VerificationFailure failureTwo = e.getVerificationFailureSet().iterator().next();
            {
                assertEquals(Errors.STATEMACHINE_WITHOUT_STATESET, failureOne.getErrorCode());
                assertEquals(Errors.STATEMACHINE_WITHOUT_TRANSITIONSET, failureTwo.getErrorCode());
            }
            {
                final String expectedMessage = getMessage(Errors.STATEMACHINE_WITHOUT_STATESET,
                        new Object[] { Negative_No_StateSet_Aand_TransitionSet.class.getName() });
                assertEquals(expectedMessage, failureOne.getErrorMessage(null));
            }
            {
                final String expectedMessage = getMessage(Errors.STATEMACHINE_WITHOUT_TRANSITIONSET,
                        new Object[] { Negative_No_StateSet_Aand_TransitionSet.class.getName() });
                assertEquals(expectedMessage, failureOne.getErrorMessage(null));
            }
            throw e;
        }
    }
}
