package net.madz.lifecycle.syntax;

import java.util.Iterator;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.LifecycleRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.StateMachineMetadataBuilder;
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
    static class CorrectLifecycleMetaSyntax {

        private String state;

        @Transition(TransitionOne.class)
        public void foo() {
        }

        public String getState() {
            return state;
        }
    }
    @LifecycleRegistry({ CorrectStateMachineSyntax.class, CorrectLifecycleMetaSyntax.class })
    @StateMachineMetadataBuilder(StateMachineMetaBuilderImpl.class)
    private static class CorrectRegistry extends AbsStateMachineRegistry {

        protected CorrectRegistry() throws VerificationException {
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

    private static class WithoutMetadataAnnotationErrorSyntax {}
    @LifecycleRegistry(WithoutMetadataAnnotationErrorSyntax.class)
    @StateMachineMetadataBuilder(StateMachineMetaBuilderImpl.class)
    private static class IncorrectStateMachineRegistry extends AbsStateMachineRegistry {

        protected IncorrectStateMachineRegistry() throws VerificationException {
            super();
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
                final String expectedErrorMessage = BundleUtils.getBundledMessage(StateMachineMetaBuilder.class,
                        "syntax_error", Errors.REGISTERED_META_ERROR,
                        new String[] { WithoutMetadataAnnotationErrorSyntax.class.getName() });
                final String actualErrorMessage = failure.getErrorMessage(null);
                assertEquals(expectedErrorMessage, actualErrorMessage);
                assertEquals(Errors.REGISTERED_META_ERROR, failure.getErrorCode());
            }
        }
    }

    @StateMachine
    public static interface CorrectStateMachineInheritanceSuperSyntax {

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
    
    @StateMachine
    public static interface CorrectStateMachineInheritanceChildSyntax extends CorrectStateMachineInheritanceSuperSyntax {}
    
    public static interface IncorrectStateMachineInheritanceSuperSyntax {

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
    
    @StateMachine
    public static interface IncorrectStateMachineInheritanceChildSyntax extends IncorrectStateMachineInheritanceSuperSyntax {}
    
    @LifecycleRegistry(IncorrectStateMachineInheritanceChildSyntax.class)
    @StateMachineMetadataBuilder(StateMachineMetaBuilderImpl.class)
    public static class IncorrectStateMachineInheritanceRegistry extends AbsStateMachineRegistry {

        protected IncorrectStateMachineInheritanceRegistry() throws VerificationException {
            super();
        }
    }
    
    @Test
    public void test_incorrect_registering_superclass_without_StateMachine() {
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

}
