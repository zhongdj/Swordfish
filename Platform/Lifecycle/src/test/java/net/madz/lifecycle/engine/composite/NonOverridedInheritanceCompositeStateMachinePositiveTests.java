package net.madz.lifecycle.engine.composite;

import static org.junit.Assert.*;
import net.madz.lifecycle.engine.composite.EngineCoreCompositeStateMachineMetadata.Contract;
import net.madz.lifecycle.engine.composite.EngineCoreCompositeStateMachineMetadata.ContractLifecycle;
import net.madz.lifecycle.engine.composite.EngineCoreCompositeStateMachineMetadata.NoOverrideComposite;
import net.madz.lifecycle.engine.composite.EngineCoreCompositeStateMachineMetadata.SM1_No_Overrides;

import org.junit.Test;


public class NonOverridedInheritanceCompositeStateMachinePositiveTests extends EngineCoreCompositeStateMachineMetadata {

    // /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Part II: composite state machine with inheritance) According to Image
    // File:
    // Composite State Machine Visibility Scope.png
    // /////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void should_support_no_overrides_relational_composite_state_machine_with_Active_contract() {
        final Contract contract = new Contract();
        assertState(ContractLifecycle.States.Draft.class, contract);
        contract.activate(); // Draft from Owning State, Active From Owning
                             // State's super State, Expire from super state.
        assertState(ContractLifecycle.States.Active.class, contract);
        final NoOverrideComposite noOverride = new NoOverrideComposite(contract);
        assertState(SM1_No_Overrides.States.S0.class, noOverride);
        noOverride.doActionT2();
        assertState(SM1_No_Overrides.States.S1.CStates.CS0.class, noOverride);
        noOverride.doActionT1();// noOverride.doActionT4();
        assertState(SM1_No_Overrides.States.S2.CStates.CS2.class, noOverride);
        noOverride.doActionT3();// noOverride.doActionT5();
        assertState(SM1_No_Overrides.States.S3.class, noOverride);
    }

    @Test
    public void should_support_no_overrides_relational_composite_state_machine_with_Active_contract_and_owning_super_T6() {
        final Contract contract = new Contract();
        assertState(ContractLifecycle.States.Draft.class, contract);
        contract.activate(); // Draft from Owning State, Active From Owning
        // State's super State, Expire from super state.
        assertState(ContractLifecycle.States.Active.class, contract);
        final NoOverrideComposite noOverride = new NoOverrideComposite(contract);
        assertState(SM1_No_Overrides.States.S0.class, noOverride);
        noOverride.doActionT2();
        assertState(SM1_No_Overrides.States.S1.CStates.CS0.class, noOverride);
        noOverride.doActionT6();// noOverride.doActionT5();
        assertState(SM1_No_Overrides.States.S2.CStates.CS2.class, noOverride);
        noOverride.doActionT6();// noOverride.doActionT5();
        assertState(SM1_No_Overrides.States.S3.class, noOverride);
    }

    @Test
    public void should_support_no_overrides_relational_composite_state_machine_with_Active_contract_and_owning_T2() {
        final Contract contract = new Contract();
        assertState(ContractLifecycle.States.Draft.class, contract);
        contract.activate(); // Draft from Owning State, Active From Owning
        // State's super State, Expire from super state.
        assertState(ContractLifecycle.States.Active.class, contract);
        final NoOverrideComposite noOverride = new NoOverrideComposite(contract);
        assertState(SM1_No_Overrides.States.S0.class, noOverride);
        noOverride.doActionT2();
        assertState(SM1_No_Overrides.States.S1.CStates.CS0.class, noOverride);
        noOverride.doActionT2();// noOverride.doActionT5();
        assertState(SM1_No_Overrides.States.S2.CStates.CS2.class, noOverride);
        noOverride.doActionT2();// noOverride.doActionT5();
        assertState(SM1_No_Overrides.States.S3.class, noOverride);
    }

    @Test
    public void should_support_no_overrides_relational_composite_state_machine_with_Draft_contract() {
        final Contract contract = new Contract();
        assertState(ContractLifecycle.States.Draft.class, contract);
        // Draft from Owning State, Active From Owning
        // State's super State, Expire from super state.
        final NoOverrideComposite noOverride = new NoOverrideComposite(contract);
        assertState(SM1_No_Overrides.States.S0.class, noOverride);
        noOverride.doActionT2();
        assertState(SM1_No_Overrides.States.S1.CStates.CS0.class, noOverride);
        noOverride.doActionT4();// noOverride.doActionT4();
        assertState(SM1_No_Overrides.States.S2.CStates.CS2.class, noOverride);
        noOverride.doActionT5();// noOverride.doActionT5();
        assertState(SM1_No_Overrides.States.S3.class, noOverride);
    }

    @Test
    public void should_support_no_overrides_relational_composite_state_machine_with_expire_contract() {
        final Contract contract = new Contract();
        assertState(ContractLifecycle.States.Draft.class, contract);
        contract.activate(); // Draft from Owning State, Active From Owning
        // State's super State, Expire from super state.
        assertState(ContractLifecycle.States.Active.class, contract);
        contract.expire();
        assertState(ContractLifecycle.States.Expired.class, contract);
        final NoOverrideComposite noOverride = new NoOverrideComposite(contract);
        assertState(SM1_No_Overrides.States.S0.class, noOverride);
        noOverride.doActionT2();
        assertState(SM1_No_Overrides.States.S1.CStates.CS0.class, noOverride);
        noOverride.doActionT1();// noOverride.doActionT4();
        assertState(SM1_No_Overrides.States.S2.CStates.CS2.class, noOverride);
        noOverride.doActionT3();// noOverride.doActionT5();
        assertState(SM1_No_Overrides.States.S3.class, noOverride);
    }
}
