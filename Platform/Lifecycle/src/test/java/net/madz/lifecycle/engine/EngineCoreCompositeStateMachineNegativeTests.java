package net.madz.lifecycle.engine;

import net.madz.lifecycle.LifecycleCommonErrors;
import net.madz.lifecycle.LifecycleException;

import org.junit.Test;

import static org.junit.Assert.fail;

public class EngineCoreCompositeStateMachineNegativeTests extends EngineCoreCompositeStateMachineMetadata {

    // /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Part I: Stand alone composite state machine (composite state machine
    // without inheritance)
    // /////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test(expected = LifecycleException.class)
    public void test_non_relational_standalone_composite_invalid_transition() {
        final ProductOrder order = new ProductOrder();
        assertState(OrderLifecycle.States.Created.class, order);
        order.start();
        assertState(OrderLifecycle.States.Started.SubStates.OrderCreated.class, order);
        try {
            order.doDeliver();
            fail("should throw LifecycleException");
        } catch (LifecycleException e) {
            try {
                assertLifecycleError(e, LifecycleCommonErrors.ILLEGAL_TRANSITION_ON_STATE,
                        OrderLifecycle.States.Started.SubTransitions.DoDeliver.class.getSimpleName(),
                        OrderLifecycle.States.Started.SubStates.OrderCreated.class.getSimpleName(), order);
            } catch (LifecycleException ex) {}
        }
        order.doProduce();
        try {
            order.confirmComplete();
            fail("should throw LifecycleException");
        } catch (LifecycleException e) {
            assertLifecycleError(e, LifecycleCommonErrors.ILLEGAL_TRANSITION_ON_STATE,
                    OrderLifecycle.States.Started.SubTransitions.ConfirmComplete.class.getSimpleName(),
                    OrderLifecycle.States.Started.SubStates.Producing.class.getSimpleName(), order);
        }
    }

    @Test(expected = LifecycleException.class)
    public void test_relational_standalone_composite_invalid_state_sharing_from_owning_valid_while() {
        final Contract contract = new Contract();
        assertState(ContractLifecycle.States.Draft.class, contract);
        final ProductOrderSharingValidWhile order = new ProductOrderSharingValidWhile(contract);
        try {
            order.start();
        } catch (LifecycleException e) {
            assertInvalidStateErrorByValidWhile(e, contract, order, ContractLifecycle.States.Active.class);
        }
        contract.activate();
        assertState(ContractLifecycle.States.Active.class, contract);
        order.start();
        assertState(RelationalOrderLifecycleSharingValidWhile.States.Started.SubStates.OrderCreated.class, order);
        contract.cancel();
        assertState(ContractLifecycle.States.Canceled.class, contract);
        try {
            order.doProduce();
        } catch (LifecycleException e) {
            assertInvalidStateErrorByValidWhile(e, contract, order, ContractLifecycle.States.Active.class);
            throw e;
        }
    }

    @Test(expected = LifecycleException.class)
    public void test_relational_standalone_composite_invalid_state_from_outer_valid_while() {
        final Contract contract = new Contract();
        assertState(ContractLifecycle.States.Draft.class, contract);
        final ProductOrderOuterValidWhile order = new ProductOrderOuterValidWhile(contract);
        try {
            order.start();
            fail("Should throw LifecycleException");
        } catch (LifecycleException e) {
            assertInvalidStateErrorByValidWhile(e, contract, order, ContractLifecycle.States.Active.class);
        }
        contract.activate();
        assertState(ContractLifecycle.States.Active.class, contract);
        order.start();
        assertState(RelationalOrderLifecycleReferencingOuterValidWhile.States.Started.SubStates.OrderCreated.class,
                order);
        contract.cancel();
        assertState(ContractLifecycle.States.Canceled.class, contract);
        try {
            order.doProduce();
            fail("Should throw LifecycleException");
        } catch (LifecycleException e) {
            assertInvalidStateErrorByValidWhile(e, contract, order, ContractLifecycle.States.Active.class);
            throw e;
        }
    }

    @Test(expected = LifecycleException.class)
    public void test_relational_standalone_composite_invalid_state_from_inner_valid_while() {
        final Contract contract = new Contract();
        assertState(ContractLifecycle.States.Draft.class, contract);
        final ProductOrderSharingValidWhile order = new ProductOrderSharingValidWhile(contract);
        try {
            order.start();
            fail("Should throw LifecycleException");
        } catch (LifecycleException e) {
            assertInvalidStateErrorByValidWhile(e, contract, order, ContractLifecycle.States.Active.class);
        }
        contract.activate();
        assertState(ContractLifecycle.States.Active.class, contract);
        order.start();
        assertState(RelationalOrderLifecycleReferencingInnerValidWhile.States.Started.SubStates.OrderCreated.class,
                order);
        contract.cancel();
        assertState(ContractLifecycle.States.Canceled.class, contract);
        try {
            order.doProduce();
            fail("Should throw LifecycleException");
        } catch (LifecycleException e) {
            assertInvalidStateErrorByValidWhile(e, contract, order, ContractLifecycle.States.Active.class);
            throw e;
        }
    }
    // /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Part II: composite state machine with inheritance)
    // /////////////////////////////////////////////////////////////////////////////////////////////////////
}
