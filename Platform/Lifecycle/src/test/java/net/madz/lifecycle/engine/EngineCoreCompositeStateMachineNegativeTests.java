package net.madz.lifecycle.engine;

import net.madz.lifecycle.LifecycleCommonErrors;
import net.madz.lifecycle.LifecycleException;
import net.madz.lifecycle.engine.EngineCoreCompositeStateMachineMetadata.Contract;
import net.madz.lifecycle.engine.EngineCoreCompositeStateMachineMetadata.ContractLifecycle;
import net.madz.lifecycle.engine.EngineCoreCompositeStateMachineMetadata.OverridesComposite;
import net.madz.lifecycle.engine.EngineCoreCompositeStateMachineMetadata.SM1_Overrides;

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
    @Test
    public void test_no_overrides_relational_composite_state_machine_with_canceled_contract() {
        final Contract contract = new Contract();
        {
            assertState(ContractLifecycle.States.Draft.class, contract);
            contract.activate(); // Draft from Owning State, Active From Owning
            assertState(ContractLifecycle.States.Active.class, contract);
        }
        final NoOverrideComposite noOverride = new NoOverrideComposite(contract);
        {
            assertState(SM1_No_Overrides.States.S0.class, noOverride);
            noOverride.doActionT2();
            assertState(SM1_No_Overrides.States.S1.CStates.CS0.class, noOverride);
        }
        {
            contract.cancel();
            assertState(ContractLifecycle.States.Canceled.class, contract);
        }
        try {
            noOverride.doActionT1();
        } catch (LifecycleException e) {
            assertInvalidStateErrorByValidWhile(e, contract, noOverride, ContractLifecycle.States.Draft.class,
                    ContractLifecycle.States.Active.class, ContractLifecycle.States.Expired.class);
        }
    }

    @Test(expected = LifecycleException.class)
    public void test_no_overrides_relational_composite_state_machine_with_T3() {
        final Contract contract = new Contract();
        {
            assertState(ContractLifecycle.States.Draft.class, contract);
            contract.activate(); // Draft from Owning State, Active From Owning
            assertState(ContractLifecycle.States.Active.class, contract);
        }
        final NoOverrideComposite noOverride = new NoOverrideComposite(contract);
        {
            assertState(SM1_No_Overrides.States.S0.class, noOverride);
            noOverride.doActionT2();
            assertState(SM1_No_Overrides.States.S1.CStates.CS0.class, noOverride);
        }
        try {
            noOverride.doActionT3();
        } catch (LifecycleException e) {
            assertLifecycleError(e, LifecycleCommonErrors.ILLEGAL_TRANSITION_ON_STATE,
                    SM1_No_Overrides.States.S2.CTransitions.T3.class,
                    SM1_No_Overrides.States.S1.CStates.CS0.class.getSimpleName(), noOverride);
        }
    }

    @Test(expected = LifecycleException.class)
    public void test_no_overrides_relational_composite_state_machine_with_T5() {
        final Contract contract = new Contract();
        {
            assertState(ContractLifecycle.States.Draft.class, contract);
            contract.activate(); // Draft from Owning State, Active From Owning
            assertState(ContractLifecycle.States.Active.class, contract);
        }
        final NoOverrideComposite noOverride = new NoOverrideComposite(contract);
        {
            assertState(SM1_No_Overrides.States.S0.class, noOverride);
            noOverride.doActionT2();
            assertState(SM1_No_Overrides.States.S1.CStates.CS0.class, noOverride);
        }
        try {
            noOverride.doActionT5();
        } catch (LifecycleException e) {
            assertLifecycleError(e, LifecycleCommonErrors.ILLEGAL_TRANSITION_ON_STATE,
                    SM2.States.S2.CTransitions.T5.class, SM1_No_Overrides.States.S1.CStates.CS0.class.getSimpleName(),
                    noOverride);
        }
    }

    @Test(expected = LifecycleException.class)
    public void test_overrides_relational_composite_state_machine_with_T1_Active_contract() {
        final Contract contract = new Contract();
        assertState(ContractLifecycle.States.Draft.class, contract);
        final OverridesComposite object = new OverridesComposite(contract);
        assertState(SM1_Overrides.States.S0.class, object);
        object.doActionT2();
        assertState(SM1_Overrides.States.S1.CStates.CS0.class, object);
        contract.activate();
        assertState(ContractLifecycle.States.Active.class, contract);
        try {
            object.doActionT1();
        } catch (LifecycleException e) {
            assertInvalidStateErrorByValidWhile(e, contract, object, ContractLifecycle.States.Expired.class,
                    ContractLifecycle.States.Draft.class);
            throw e;
        }
    }

    @Test(expected = LifecycleException.class)
    public void test_overrides_relational_composite_state_machine_with_T2_Active_contract() {
        final Contract contract = new Contract();
        assertState(ContractLifecycle.States.Draft.class, contract);
        final OverridesComposite object = new OverridesComposite(contract);
        assertState(SM1_Overrides.States.S0.class, object);
        object.doActionT2();
        assertState(SM1_Overrides.States.S1.CStates.CS0.class, object);
        contract.activate();
        assertState(ContractLifecycle.States.Active.class, contract);
        try {
            object.doActionT2();
        } catch (LifecycleException e) {
            assertInvalidStateErrorByValidWhile(e, contract, object, ContractLifecycle.States.Expired.class,
                    ContractLifecycle.States.Draft.class);
            throw e;
        }
    }

    @Test(expected = LifecycleException.class)
    public void test_overrides_relational_composite_state_machine_with_T6_Active_contract() {
        final Contract contract = new Contract();
        assertState(ContractLifecycle.States.Draft.class, contract);
        contract.activate();
        assertState(ContractLifecycle.States.Active.class, contract);
        final OverridesComposite object = new OverridesComposite(contract);
        assertState(SM1_Overrides.States.S0.class, object);
        object.doActionT2();
        assertState(SM1_Overrides.States.S1.CStates.CS0.class, object);
        try {
            object.doActionT6();
        } catch (LifecycleException e) {
            assertInvalidStateErrorByValidWhile(e, contract, object, ContractLifecycle.States.Expired.class,
                    ContractLifecycle.States.Draft.class);
            throw e;
        }
    }

    @Test(expected = LifecycleException.class)
    public void test_overrides_relational_composite_state_machine_with_T6_Canceled_contract() {
        final Contract contract = new Contract();
        assertState(ContractLifecycle.States.Draft.class, contract);
        contract.activate();
        assertState(ContractLifecycle.States.Active.class, contract);
        contract.cancel();
        assertState(ContractLifecycle.States.Canceled.class, contract);
        final OverridesComposite object = new OverridesComposite(contract);
        assertState(SM1_Overrides.States.S0.class, object);
        object.doActionT2();
        assertState(SM1_Overrides.States.S1.CStates.CS0.class, object);
        try {
            object.doActionT6();
        } catch (LifecycleException e) {
            assertInvalidStateErrorByValidWhile(e, contract, object, ContractLifecycle.States.Expired.class,
                    ContractLifecycle.States.Draft.class);
            throw e;
        }
    }

    @Test(expected = LifecycleException.class)
    public void test_overrides_relational_composite_state_machine_with_T6_Expired_contract() {
        final Contract contract = new Contract();
        assertState(ContractLifecycle.States.Draft.class, contract);
        contract.activate();
        assertState(ContractLifecycle.States.Active.class, contract);
        contract.expire();
        assertState(ContractLifecycle.States.Expired.class, contract);
        final OverridesComposite object = new OverridesComposite(contract);
        assertState(SM1_Overrides.States.S0.class, object);
        object.doActionT2();
        assertState(SM1_Overrides.States.S1.CStates.CS0.class, object);
        try {
            object.doActionT6();
        } catch (LifecycleException e) {
            assertLifecycleError(e, LifecycleCommonErrors.ILLEGAL_TRANSITION_ON_STATE,
                    SM1_Overrides.Transitions.T6.class, SM1_Overrides.States.S1.CStates.CS0.class.getSimpleName(),
                    object);
        }
    }

    @Test(expected = LifecycleException.class)
    public void test_overrides_relational_composite_state_machine_with_T6_Draft_contract() {
        final Contract contract = new Contract();
        assertState(ContractLifecycle.States.Draft.class, contract);
        final OverridesComposite object = new OverridesComposite(contract);
        assertState(SM1_Overrides.States.S0.class, object);
        object.doActionT2();
        assertState(SM1_Overrides.States.S1.CStates.CS0.class, object);
        try {
            object.doActionT6();
        } catch (LifecycleException e) {
            assertLifecycleError(e, LifecycleCommonErrors.ILLEGAL_TRANSITION_ON_STATE,
                    SM1_Overrides.Transitions.T6.class, SM1_Overrides.States.S1.CStates.CS0.class.getSimpleName(),
                    object);
        }
    }
}
