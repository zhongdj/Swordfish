package net.madz.lifecycle.syntax;

import java.util.Iterator;

import net.madz.lifecycle.AbstractStateMachineRegistry;
import net.madz.lifecycle.AbstractStateMachineRegistry.LifecycleRegistry;
import net.madz.lifecycle.AbstractStateMachineRegistry.StateMachineMetadataBuilder;
import net.madz.lifecycle.Errors;
import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.meta.builder.StateMachineMetaBuilder;
import net.madz.lifecycle.meta.impl.builder.StateMachineMetaBuilderImpl;
import net.madz.lifecycle.syntax.RegisterSyntaxTest.CorrectStateMachineSyntax.Transitions.TransitionOne;
import net.madz.utils.BundleUtils;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailure;
import net.madz.verification.VerificationFailureSet;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RegisterSyntaxTest {

    @StateMachine
    public static interface CorrectStateMachineSyntax {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = TransitionOne.class, value = StateB.class)
            static interface StateA {}
            @End
            static interface StateB {}
        }
        @TransitionSet
        static interface Transitions {

            static interface TransitionOne {}
        }
    }
    @LifecycleMeta(CorrectStateMachineSyntax.class)
    private static class CorrectLifecycleMetaSyntax {

        private String state;

        @Transition(TransitionOne.class)
        public void foo() {
        }
    }
    private static class WithoutMetadataAnnotationErrorSyntax {}
    
    @LifecycleRegistry({ CorrectStateMachineSyntax.class, CorrectLifecycleMetaSyntax.class })
    @StateMachineMetadataBuilder(StateMachineMetaBuilderImpl.class)
    private static class CorrectRegistry extends AbstractStateMachineRegistry {

        protected CorrectRegistry() throws VerificationException {
            super();
        }
    }
    @LifecycleRegistry(WithoutMetadataAnnotationErrorSyntax.class)
    @StateMachineMetadataBuilder(StateMachineMetaBuilderImpl.class)
    private static class IncorrectStateMachineRegistry extends AbstractStateMachineRegistry {

        protected IncorrectStateMachineRegistry() throws VerificationException {
            super();
        }
    }

    @Test
    public void test_correct_metadata_without_syntax_error() {
        try {
            new CorrectRegistry();
        } catch (VerificationException e) {
            fail("No Exception expected");
        }
    }

    @Test
    public void test_incorrect_registering_without_StateMachine_or_LifecycleMeta() {
        try {
            new IncorrectStateMachineRegistry();
            fail("Verification Exception expected.");
        } catch (VerificationException ex) {
            VerificationFailureSet failureSet = ex.getVerificationFailureSet();
            Iterator<VerificationFailure> iterator = failureSet.iterator();
            assertEquals(1, failureSet.size());
            while ( iterator.hasNext() ) {
                VerificationFailure failure = iterator.next();
                final String expectedErrorMessage = BundleUtils.getBundledMessage(StateMachineMetaBuilder.class, "syntax_error",
                        Errors.REGISTERED_META_ERROR, new String[] { WithoutMetadataAnnotationErrorSyntax.class.getName() });
                final String actualErrorMessage = failure.getErrorMessage(null);
                assertEquals(expectedErrorMessage, actualErrorMessage);
                assertEquals(Errors.REGISTERED_META_ERROR, failure.getErrorCode());
            }
        }
    }
}
