package net.madz.lifecycle.syntax.lm.relation;

import static org.junit.Assert.fail;
import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.LifecycleRegistry;
import net.madz.lifecycle.AbsStateMachineRegistry.StateMachineBuilder;
import net.madz.verification.VerificationException;

import org.junit.Test;

public class LMSyntaxRelationPositiveTest extends LMSyntaxRelationMetadata {

    @Test
    public final void test_relations_coverage_in_simple_stateMachine() {
        @LifecycleRegistry(PLM_5.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            e.printStackTrace();
            fail("No exception expected!");
        }
    }

    @Test
    public final void test_relations_coverage_in_stateMachine_with_composite_stateMachines() {
        @LifecycleRegistry(PLM_6.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            e.printStackTrace();
            fail("No exception expected!");
        }
    }

    @Test
    public final void test_relations_coverage_in_stateMachine_with_superSateMachine() {
        @LifecycleRegistry(PLM_7.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
        } catch (VerificationException e) {
            e.printStackTrace();
            fail("No exception expected!");
        }
    }

    @Test
    public final void test_extended_relation_metadata() {
        @LifecycleRegistry(LevelThreeOrder.class)
        @StateMachineBuilder
        class Registry extends AbsStateMachineRegistry {

            protected Registry() throws VerificationException {}
        }
        try {
            new Registry();
            LevelThreeCustomer levelThreeCustomer = new LevelThreeCustomer();
            levelThreeCustomer.activate();
            levelThreeCustomer.creditRate();
            levelThreeCustomer.prepay();
            LevelThreeOrder levelThreeOrder = new LevelThreeOrder(levelThreeCustomer);
            levelThreeOrder.pay();
        } catch (VerificationException e) {
            e.printStackTrace();
            fail("No exception expected!");
        }
    }
}
