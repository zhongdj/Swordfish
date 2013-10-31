package net.madz.lifecycle.syntax;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.LifecycleRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.StateMachineBuilder;
import net.madz.lifecycle.Errors;
import net.madz.lifecycle.annotations.relation.InboundWhile;
import net.madz.lifecycle.annotations.relation.RelateTo;
import net.madz.verification.VerificationException;

import org.junit.Test;

public class RelationSyntaxNegativeTest extends RelationSyntaxMetadata {

    @Test(expected = VerificationException.class)
    public void test_relation_to_refers_to_invalid_statemachine() throws VerificationException {
        @LifecycleRegistry(NStandalone3.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    Errors.RELATION_RELATED_TO_REFER_TO_NON_STATEMACHINE,
                    NStandalone3.Relations.NR.class.getAnnotation(RelateTo.class));
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_multiple_relationset() throws VerificationException {
        @LifecycleRegistry(NStandalone4.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(), Errors.RELATIONSET_MULTIPLE,
                    NStandalone4.class);
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_relation_not_defined_in_relation_set() throws VerificationException {
        @LifecycleRegistry(NStandalone.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    Errors.RELATION_INBOUNDWHILE_RELATION_NOT_DEFINED_IN_RELATIONSET, NStandalone.States.NA.class
                            .getAnnotation(InboundWhile.class).relation(), NStandalone.States.NA.class,
                    NStandalone.class.getName());
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_relation_on_not_matching_relation() throws VerificationException {
        @LifecycleRegistry(NStandalone2.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    Errors.RELATION_ON_ATTRIBUTE_OF_INBOUNDWHILE_NOT_MATCHING_RELATION,
                    NStandalone2.States.NA.class.getAnnotation(InboundWhile.class), NStandalone2.States.NA.class,
                    RelatedSM.class.getName());
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_inheritance_relation_not_defined_in_relation_set() throws VerificationException {
        @LifecycleRegistry(NChild.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    Errors.RELATION_INBOUNDWHILE_RELATION_NOT_DEFINED_IN_RELATIONSET, NChild.States.NCC.class
                            .getAnnotation(InboundWhile.class).relation(), NChild.States.NCC.class,
                    NChild.class.getName());
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_inheritance_relation_on_not_matching_relation() throws VerificationException {
        @LifecycleRegistry(NChild2.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    Errors.RELATION_ON_ATTRIBUTE_OF_INBOUNDWHILE_NOT_MATCHING_RELATION,
                    NChild2.States.NC2C.class.getAnnotation(InboundWhile.class), NChild2.States.NC2C.class,
                    RelatedSM.class.getName());
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_inheritance_validWhile_relation_not_defined_in_relation_set() throws VerificationException {
        @LifecycleRegistry(NChild3.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    Errors.RELATION_INBOUNDWHILE_RELATION_NOT_DEFINED_IN_RELATIONSET, NChild3.States.NC3C.class
                            .getAnnotation(InboundWhile.class).relation(), NChild3.States.NC3C.class,
                    NChild3.class.getName());
            throw e;
        }
    }

    @Test(expected = VerificationException.class)
    public void test_inheritance_validWhile_relation_on_not_matching_relation() throws VerificationException {
        @LifecycleRegistry(NChild4.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            assertFailure(e.getVerificationFailureSet().iterator().next(),
                    Errors.RELATION_ON_ATTRIBUTE_OF_INBOUNDWHILE_NOT_MATCHING_RELATION,
                    NChild4.States.NC4C.class.getAnnotation(InboundWhile.class), NChild4.States.NC4C.class,
                    RelatedSM.class.getName());
            throw e;
        }
    }
}
