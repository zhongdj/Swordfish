package net.madz.lifecycle.syntax;

import java.util.Iterator;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.LifecycleRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.StateMachineMetadataBuilder;
import net.madz.lifecycle.Errors;
import net.madz.lifecycle.meta.impl.builder.StateMachineMetaBuilderImpl;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailure;
import net.madz.verification.VerificationFailureSet;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StateSetAndTransitionSetSyntaxNegativeTest extends StateSetSyntaxMetadata {

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
        @LifecycleRegistry(Negative_No_StateSet_and_TransitionSet.class)
        @StateMachineMetadataBuilder(StateMachineMetaBuilderImpl.class)
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertEquals(2, e.getVerificationFailureSet().size());
            Iterator<VerificationFailure> iterator = e.getVerificationFailureSet().iterator();
            final VerificationFailure failureOne = iterator.next();
            final VerificationFailure failureTwo = iterator.next();
            {
                assertEquals(Errors.STATEMACHINE_WITHOUT_STATESET, failureOne.getErrorCode());
                assertEquals(Errors.STATEMACHINE_WITHOUT_TRANSITIONSET, failureTwo.getErrorCode());
            }
            {
                final String expectedMessage = getMessage(Errors.STATEMACHINE_WITHOUT_STATESET,
                        new Object[] { Negative_No_StateSet_and_TransitionSet.class.getName() });
                assertEquals(expectedMessage, failureOne.getErrorMessage(null));
            }
            {
                final String expectedMessage = getMessage(Errors.STATEMACHINE_WITHOUT_TRANSITIONSET,
                        new Object[] { Negative_No_StateSet_and_TransitionSet.class.getName() });
                assertEquals(expectedMessage, failureTwo.getErrorMessage(null));
            }
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_StateMachine_with_Multi_StateSet_And_Multi_TransitionSet() throws VerificationException {
        @LifecycleRegistry({ Negative_Multi_StateSet_Multi_TransitionSet.class })
        @StateMachineMetadataBuilder(StateMachineMetaBuilderImpl.class)
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException ex) {
            assertEquals(2, ex.getVerificationFailureSet().size());
            Iterator<VerificationFailure> iterator = ex.getVerificationFailureSet().iterator();
            final VerificationFailure failureOne = iterator.next();
            final VerificationFailure failureTwo = iterator.next();
            assertFailure(failureOne, Errors.STATEMACHINE_MULTIPLE_STATESET,
                    new Object[] { Negative_Multi_StateSet_Multi_TransitionSet.class.getName() });
            assertFailure(failureTwo, Errors.STATEMACHINE_MULTIPLE_TRANSITIONSET,
                    new Object[] { Negative_Multi_StateSet_Multi_TransitionSet.class.getName() });
            throw ex;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_StateSet_no_states() throws VerificationException {
        @LifecycleRegistry({ Negative_No_State_No_Transition.class })
        @StateMachineMetadataBuilder(StateMachineMetaBuilderImpl.class)
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            Iterator<VerificationFailure> iterator = e.getVerificationFailureSet().iterator();
            assertFailure(iterator.next(), Errors.STATESET_WITHOUT_STATE,
                    new Object[] { Negative_No_State_No_Transition.States.class.getName() });
            assertFailure(iterator.next(), Errors.TRANSITIONSET_WITHOUT_TRANSITION,
                    new Object[] { Negative_No_State_No_Transition.Transitions.class.getName() });
            throw e;
        }
    }

    private void assertFailure(VerificationFailure failure, String errorCode, Object... args) {
        assertEquals(errorCode, failure.getErrorCode());
        final String expectedMessage = getMessage(errorCode, args);
        assertEquals(expectedMessage, failure.getErrorMessage(null));
    }

    @Test(expected = VerificationException.class)
    public void test_StateSet_Without_InitialState_And_EndState() throws VerificationException {
        @LifecycleRegistry(Negative_StateSet_Without_InitalState_And_EndState.class)
        @StateMachineMetadataBuilder(StateMachineMetaBuilderImpl.class)
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            VerificationFailureSet failureSet = e.getVerificationFailureSet();
            assertEquals(2, failureSet.size());
            Iterator<VerificationFailure> iterator = failureSet.iterator();
            VerificationFailure failureOne = iterator.next();
            VerificationFailure failureTwo = iterator.next();
            assertFailure(failureOne, Errors.STATESET_WITHOUT_INITAL_STATE,
                    Negative_StateSet_Without_InitalState_And_EndState.States.class.getName());
            assertFailure(failureTwo, Errors.STATESET_WITHOUT_FINAL_STATE,
                    Negative_StateSet_Without_InitalState_And_EndState.States.class.getName());
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_StateSet_With_Multi_InitialState() throws VerificationException {
        @LifecycleRegistry(Negative_StateSet_With_Multi_InitalState.class)
        @StateMachineMetadataBuilder(StateMachineMetaBuilderImpl.class)
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(), Errors.STATESET_MULTIPLE_INITAL_STATES,
                    Negative_StateSet_With_Multi_InitalState.States.class.getName());
            throw e;
        }
    }
}
