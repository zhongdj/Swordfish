package net.madz.lifecycle.syntax;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.LifecycleRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.StateMachineBuilder;
import net.madz.lifecycle.Errors;
import net.madz.lifecycle.meta.impl.builder.StateMachineMetaBuilderImpl;
import net.madz.verification.VerificationException;

import org.junit.Test;

public class StateIndicatorNegativeTest extends StateIndicatorMetadata {

    @Test(expected = VerificationException.class)
    public void test_no_default_state_indicator() throws VerificationException {
        @LifecycleRegistry(StateIndicatorMetadata.NNoDefaultStateIndicatorInterface.class)
        @StateMachineBuilder(StateMachineMetaBuilderImpl.class)
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {
                super();
            }
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    Errors.STATE_INDICATOR_CANNOT_FIND_DEFAULT_AND_SPECIFIED_STATE_INDICATOR,
                    NNoDefaultStateIndicatorInterface.class);
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_default_state_indicator_error_class_impl() throws Exception {
        @LifecycleRegistry(StateIndicatorMetadata.NDefaultPublicStateSetterClass.class)
        @StateMachineBuilder(StateMachineMetaBuilderImpl.class)
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {
                super();
            }
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    Errors.STATE_INDICATOR_CANNOT_EXPOSE_STATE_INDICATOR_SETTER,
                    NDefaultPublicStateSetterClass.class.getDeclaredMethod("setState", String.class));
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_default_state_indicator_error_interface_impl() throws Exception {
        @LifecycleRegistry(StateIndicatorMetadata.NDefaultStateIndicatorInterface.class)
        @StateMachineBuilder(StateMachineMetaBuilderImpl.class)
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {
                super();
            }
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    Errors.STATE_INDICATOR_CANNOT_EXPOSE_STATE_INDICATOR_SETTER,
                    NNoDefaultStateIndicatorInterface.class.getDeclaredMethod("setState", String.class));
            throw e;
        }
    }
    @Test(expected = VerificationException.class)
    public void test_field_access_expose_state_indicator() throws Exception {
        @LifecycleRegistry(StateIndicatorMetadata.NPublicStateFieldClass.class)
        @StateMachineBuilder(StateMachineMetaBuilderImpl.class)
        class Registry extends AbsStateMachineRegistry {
            
            protected Registry() throws VerificationException {
                super();
            }
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    Errors.STATE_INDICATOR_CANNOT_EXPOSE_STATE_INDICATOR_FIELD,
                    NNoDefaultStateIndicatorInterface.class.getDeclaredField("state"));
            throw e;
        }
    }
    
    
}
