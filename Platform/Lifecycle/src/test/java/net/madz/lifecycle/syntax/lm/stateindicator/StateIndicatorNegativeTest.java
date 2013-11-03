package net.madz.lifecycle.syntax.lm.stateindicator;

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
                    NDefaultStateIndicatorInterface.class.getDeclaredMethod("setState", String.class));
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
                    NPublicStateFieldClass.class.getDeclaredField("state"));
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_expose_state_indicator_error_interface_impl() throws Exception {
        @LifecycleRegistry(StateIndicatorMetadata.NPublicStateSetterClass.class)
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
                    NPublicStateSetterClass.class.getDeclaredMethod("setState", String.class));
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_expose_state_indicator_error_class_impl() throws Exception {
        @LifecycleRegistry(StateIndicatorMetadata.NPublicStateIndicatorInterface.class)
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
                    NPublicStateIndicatorInterface.class.getDeclaredMethod("setState", String.class));
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_state_indicator_setter_not_found_class_impl() throws Exception {
        @LifecycleRegistry(StateIndicatorMetadata.NStateIndicatorSetterNotFound.class)
        @StateMachineBuilder(StateMachineMetaBuilderImpl.class)
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {
                super();
            }
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(), Errors.STATE_INDICATOR_SETTER_NOT_FOUND,
                    NStateIndicatorSetterNotFound.class);
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_state_indicator_need_converter_interface_impl() throws Exception {
        @LifecycleRegistry(StateIndicatorMetadata.NNeedConverter.class)
        @StateMachineBuilder(StateMachineMetaBuilderImpl.class)
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {
                super();
            }
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(), Errors.STATE_INDICATOR_CONVERTER_NOT_FOUND,
                    NNeedConverter.class, Integer.class);
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_state_indicator_converter_invalid_interface_impl() throws Exception {
        @LifecycleRegistry(StateIndicatorMetadata.NStateIndicatorConverterInvalid.class)
        @StateMachineBuilder(StateMachineMetaBuilderImpl.class)
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {
                super();
            }
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(), Errors.STATE_INDICATOR_CONVERTER_INVALID,
                    NStateIndicatorConverterInvalid.class, Object.class, StateConverterImpl.class, Integer.class);
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_multiple_state_indicator_error() throws Exception {
        @LifecycleRegistry(StateIndicatorMetadata.NegativeMultipleStateIndicator.class)
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
                    Errors.STATE_INDICATOR_MULTIPLE_STATE_INDICATOR_ERROR, NegativeMultipleStateIndicator.class);
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_multiple_state_indicator_through_hierarchy_error() throws Exception {
        @LifecycleRegistry(StateIndicatorMetadata.NegativeMultipleStateIndicatorChild.class)
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
                    Errors.STATE_INDICATOR_MULTIPLE_STATE_INDICATOR_ERROR, NegativeMultipleStateIndicatorChild.class);
            throw e;
        }
    }
}
