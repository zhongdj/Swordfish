package net.madz.lifecycle.syntax.lm.stateindicator;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.LifecycleRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.StateMachineBuilder;
import net.madz.lifecycle.meta.impl.builder.StateMachineMetaBuilderImpl;
import net.madz.verification.VerificationException;

import org.junit.Test;

public class StateIndicatorPositiveTest extends StateIndicatorMetadata {

    @Test
    public void test_default_state_indicator_interface_impl() throws VerificationException {
        @LifecycleRegistry({ StateIndicatorMetadata.PDefaultStateIndicatorInterface.class })
        @StateMachineBuilder(StateMachineMetaBuilderImpl.class)
        class CorrectRegistry extends AbsStateMachineRegistry {

            protected CorrectRegistry() throws VerificationException {
                super();
            }
        }
        new CorrectRegistry();
    }

    @Test
    public void test_default_state_indicator_class_impl() throws VerificationException {
        @LifecycleRegistry({ StateIndicatorMetadata.PDefaultPrivateStateSetterClass.class })
        @StateMachineBuilder(StateMachineMetaBuilderImpl.class)
        class CorrectRegistry extends AbsStateMachineRegistry {

            protected CorrectRegistry() throws VerificationException {
                super();
            }
        }
        new CorrectRegistry();
    }

    @Test
    public void test_field_access_state_indicator_class_impl() throws VerificationException {
        @LifecycleRegistry({ StateIndicatorMetadata.PrivateStateFieldClass.class,
                StateIndicatorMetadata.PrivateStateFieldConverterClass.class })
        @StateMachineBuilder(StateMachineMetaBuilderImpl.class)
        class CorrectRegistry extends AbsStateMachineRegistry {

            protected CorrectRegistry() throws VerificationException {
                super();
            }
        }
        new CorrectRegistry();
    }

    @Test
    public void test_property_access_state_indicator_class_impl() throws VerificationException {
        @LifecycleRegistry({ StateIndicatorMetadata.PrivateStateSetterClass.class,
                StateIndicatorMetadata.PStateIndicatorInterface.class, PStateIndicatorConverterInterface.class })
        @StateMachineBuilder(StateMachineMetaBuilderImpl.class)
        class CorrectRegistry extends AbsStateMachineRegistry {

            protected CorrectRegistry() throws VerificationException {
                super();
            }
        }
        new CorrectRegistry();
    }
}
