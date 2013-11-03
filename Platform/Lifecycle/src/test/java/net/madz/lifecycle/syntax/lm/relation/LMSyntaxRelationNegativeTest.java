package net.madz.lifecycle.syntax.lm.relation;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.LifecycleRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.StateMachineBuilder;
import net.madz.lifecycle.syntax.lm.relation.LMSyntaxRelationMetadata.S4;
import net.madz.lifecycle.Errors;
import net.madz.verification.VerificationException;

import org.junit.Test;

public class LMSyntaxRelationNegativeTest extends LMSyntaxRelationMetadata {

    @Test(expected = VerificationException.class)
    public final void test_inboundwhile_relation_not_coveraged() throws VerificationException {
        @LifecycleRegistry(NLM_1.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public final void test_validwhile_relation_not_coveraged() throws VerificationException {
        @LifecycleRegistry(NLM_2.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public final void test_relation_in_composite_stateMachine_not_coveraged() throws VerificationException {
        @LifecycleRegistry(NLM_3.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public final void test_relation_in_super_stateMachine_not_coveraged() throws VerificationException {
        @LifecycleRegistry(NLM_4.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public final void test_LM_reference_an_invalid_relation_on_field() throws VerificationException {
        @LifecycleRegistry(NLM_5.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    Errors.LM_REFERENCE_INVALID_RELATION_INSTANCE, NLM_5.class.getName(),
                    S4.Relations.R1.class.getName(), S5.class.getName());
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public final void test_LM_reference_an_invalid_relation_on_property() throws VerificationException {
        @LifecycleRegistry(NLM_6.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    Errors.LM_REFERENCE_INVALID_RELATION_INSTANCE, NLM_6.class.getName(),
                    S4.Relations.R1.class.getName(), R1_S.class.getName());
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public final void test_LM_relation_defined_multi_times_in_stateMachine() throws VerificationException {
        @LifecycleRegistry(NLM_7.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(), Errors.LM_RELATION_INSTANCE_MUST_BE_UNIQUE,
                    NLM_7.class.getName(), S4.Relations.R3.class.getName());
            throw e;
        }
    }
}
