package net.madz.lifecycle.engine;

import net.madz.lifecycle.annotations.CompositeStateMachine;
import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.Functions;
import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.relation.RelateTo;
import net.madz.lifecycle.annotations.relation.Relation;
import net.madz.lifecycle.annotations.relation.RelationSet;
import net.madz.lifecycle.annotations.relation.ValidWhile;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.annotations.state.ShortCut;
import net.madz.verification.VerificationException;

import org.junit.BeforeClass;

public class EngineCoreCompositeStateMachineMetadata extends EngineTestBase {

    @BeforeClass
    public static void registerLifecycleMetadata() throws VerificationException {
        registerMetaFromClass(EngineCoreCompositeStateMachineMetadata.class);
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // Non Relational
    // ///////////////////////////////////////////////////////////////////////////////
    @StateMachine
    static interface OrderLifecycle {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = Transitions.Start.class, value = Started.class)
            static interface Created {}
            @CompositeStateMachine
            @Function(transition = Transitions.Cancel.class, value = Canceled.class)
            static interface Started {

                @StateSet
                static interface SubStates {

                    @Initial
                    @Function(transition = OrderLifecycle.States.Started.SubTransitions.DoProduce.class,
                            value = Producing.class)
                    static interface OrderCreated {}
                    @Function(transition = OrderLifecycle.States.Started.SubTransitions.DoDeliver.class,
                            value = Delivering.class)
                    static interface Producing {}
                    @Function(transition = OrderLifecycle.States.Started.SubTransitions.ConfirmComplete.class,
                            value = Done.class)
                    static interface Delivering {}
                    @End
                    @ShortCut(OrderLifecycle.States.Finished.class)
                    static interface Done {}
                }
                @TransitionSet
                static interface SubTransitions {

                    static interface DoProduce {}
                    static interface DoDeliver {}
                    static interface ConfirmComplete {}
                }
            }
            @End
            static interface Finished {}
            @End
            static interface Canceled {}
        }
        @TransitionSet
        static interface Transitions {

            static interface Start {}
            static interface Cancel {}
        }
    }
    public abstract static class ProductBase extends ReactiveObject {
    }
    @LifecycleMeta(OrderLifecycle.class)
    public static class ProductOrder extends ProductBase {

        public ProductOrder() {
            initialState(OrderLifecycle.States.Created.class.getSimpleName());
        }

        @Transition
        public void start() {}

        @Transition
        public void cancel() {}

        @Transition
        public void doProduce() {}

        @Transition
        public void doDeliver() {}

        @Transition
        public void confirmComplete() {}
    }
    // ///////////////////////////////////////////////////////////////////////////////
    // Relational
    // ///////////////////////////////////////////////////////////////////////////////
    @StateMachine
    static interface ContractLifecycle {

        @StateSet
        static interface States {

            @Initial
            @Functions({ @Function(transition = ContractLifecycle.Transitions.Activate.class, value = Active.class),
                    @Function(transition = ContractLifecycle.Transitions.Cancel.class, value = Canceled.class) })
            static interface Draft {}
            @Functions({ @Function(transition = ContractLifecycle.Transitions.Expire.class, value = Expired.class),
                    @Function(transition = ContractLifecycle.Transitions.Cancel.class, value = Canceled.class) })
            static interface Active {}
            @End
            static interface Expired {}
            @End
            static interface Canceled {}
        }
        @TransitionSet
        static interface Transitions {

            static interface Activate {}
            static interface Expire {}
            static interface Cancel {}
        }
    }
    @LifecycleMeta(ContractLifecycle.class)
    public static class Contract extends ReactiveObject {

        public Contract() {
            initialState(ContractLifecycle.States.Draft.class.getSimpleName());
        }

        @Transition
        public void activate() {}

        @Transition
        public void expire() {}

        @Transition
        public void cancel() {}
    }
    // ///////////////////////////////////////////////////////////////////////////////
    // Relational Case 1.
    // ///////////////////////////////////////////////////////////////////////////////
    @StateMachine
    static interface RelationalOrderLifecycleSharingValidWhile {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = Transitions.Start.class, value = Started.class)
            @ValidWhile(on = { ContractLifecycle.States.Active.class }, relation = Relations.Contract.class)
            static interface Created {}
            @CompositeStateMachine
            @Function(transition = Transitions.Cancel.class, value = Canceled.class)
            static interface Started {

                @StateSet
                static interface SubStates {

                    @Initial
                    @Function(
                            transition = RelationalOrderLifecycleSharingValidWhile.States.Started.SubTransitions.DoProduce.class,
                            value = Producing.class)
                    @ValidWhile(on = { ContractLifecycle.States.Active.class },
                            relation = RelationalOrderLifecycleSharingValidWhile.Relations.Contract.class)
                    static interface OrderCreated {}
                    @Function(
                            transition = RelationalOrderLifecycleSharingValidWhile.States.Started.SubTransitions.DoDeliver.class,
                            value = Delivering.class)
                    @ValidWhile(on = { ContractLifecycle.States.Active.class },
                            relation = RelationalOrderLifecycleSharingValidWhile.Relations.Contract.class)
                    static interface Producing {}
                    @Function(
                            transition = RelationalOrderLifecycleSharingValidWhile.States.Started.SubTransitions.ConfirmComplete.class,
                            value = Done.class)
                    @ValidWhile(on = { ContractLifecycle.States.Active.class },
                            relation = RelationalOrderLifecycleSharingValidWhile.Relations.Contract.class)
                    static interface Delivering {}
                    @End
                    @ShortCut(RelationalOrderLifecycleSharingValidWhile.States.Finished.class)
                    // Ignoring : @ValidWhile(on = {
                    // ContractLifecycle.States.Active.class }, relation =
                    // Relations.Contract.class)
                    static interface Done {}
                }
                @TransitionSet
                static interface SubTransitions {

                    static interface DoProduce {}
                    static interface DoDeliver {}
                    static interface ConfirmComplete {}
                }
            }
            @End
            static interface Finished {}
            @End
            static interface Canceled {}
        }
        @TransitionSet
        static interface Transitions {

            static interface Start {}
            static interface Cancel {}
        }
        @RelationSet
        static interface Relations {

            @RelateTo(ContractLifecycle.class)
            static interface Contract {}
        }
    }
    @LifecycleMeta(RelationalOrderLifecycleSharingValidWhile.class)
    public static class ProductOrderSharingValidWhile extends ProductBase {

        private Contract contract;

        public ProductOrderSharingValidWhile(Contract contract) {
            this.contract = contract;
            initialState(RelationalOrderLifecycleSharingValidWhile.States.Created.class.getSimpleName());
        }

        @Relation(RelationalOrderLifecycleSharingValidWhile.Relations.Contract.class)
        public Contract getContract() {
            return this.contract;
        }

        @Transition
        public void start() {}

        @Transition
        public void cancel() {}

        @Transition
        public void doProduce() {}

        @Transition
        public void doDeliver() {}

        @Transition
        public void confirmComplete() {}
    }
    // ///////////////////////////////////////////////////////////////////////////////
    // Relational Case 2.
    // ///////////////////////////////////////////////////////////////////////////////
    @StateMachine
    static interface RelationalOrderLifecycleReferencingOuterValidWhile {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = Transitions.Start.class, value = Started.class)
            @ValidWhile(on = { ContractLifecycle.States.Active.class }, relation = Relations.Contract.class)
            static interface Created {}
            @CompositeStateMachine
            @Function(transition = Transitions.Cancel.class, value = Canceled.class)
            @ValidWhile(on = { ContractLifecycle.States.Active.class }, relation = Relations.Contract.class)
            static interface Started {

                @StateSet
                static interface SubStates {

                    @Initial
                    @Function(
                            transition = RelationalOrderLifecycleReferencingOuterValidWhile.States.Started.SubTransitions.DoProduce.class,
                            value = Producing.class)
                    static interface OrderCreated {}
                    @Function(
                            transition = RelationalOrderLifecycleReferencingOuterValidWhile.States.Started.SubTransitions.DoDeliver.class,
                            value = Delivering.class)
                    static interface Producing {}
                    @Function(
                            transition = RelationalOrderLifecycleReferencingOuterValidWhile.States.Started.SubTransitions.ConfirmComplete.class,
                            value = Done.class)
                    static interface Delivering {}
                    @End
                    @ShortCut(RelationalOrderLifecycleReferencingOuterValidWhile.States.Finished.class)
                    static interface Done {}
                }
                @TransitionSet
                static interface SubTransitions {

                    static interface DoProduce {}
                    static interface DoDeliver {}
                    static interface ConfirmComplete {}
                }
            }
            @End
            static interface Finished {}
            @End
            static interface Canceled {}
        }
        @TransitionSet
        static interface Transitions {

            static interface Start {}
            static interface Cancel {}
        }
        @RelationSet
        static interface Relations {

            @RelateTo(ContractLifecycle.class)
            static interface Contract {}
        }
    }
    @LifecycleMeta(RelationalOrderLifecycleReferencingOuterValidWhile.class)
    public static class ProductOrderOuterValidWhile extends ProductBase {

        private final Contract contract;

        public ProductOrderOuterValidWhile(Contract contract) {
            initialState(RelationalOrderLifecycleReferencingOuterValidWhile.States.Created.class.getSimpleName());
            this.contract = contract;
        }

        @Relation(RelationalOrderLifecycleReferencingOuterValidWhile.Relations.Contract.class)
        public Contract getContract() {
            return this.contract;
        }

        @Transition
        public void start() {}

        @Transition
        public void cancel() {}

        @Transition
        public void doProduce() {}

        @Transition
        public void doDeliver() {}

        @Transition
        public void confirmComplete() {}
    }
    @StateMachine
    static interface RelationalOrderLifecycleReferencingInnerValidWhile {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = Transitions.Start.class, value = Started.class)
            static interface Created {}
            @CompositeStateMachine
            @Function(transition = Transitions.Cancel.class, value = Canceled.class)
            static interface Started {

                @StateSet
                static interface SubStates {

                    @Initial
                    @Function(
                            transition = RelationalOrderLifecycleReferencingInnerValidWhile.States.Started.SubTransitions.DoProduce.class,
                            value = Producing.class)
                    static interface OrderCreated {}
                    @Function(
                            transition = RelationalOrderLifecycleReferencingInnerValidWhile.States.Started.SubTransitions.DoDeliver.class,
                            value = Delivering.class)
                    static interface Producing {}
                    @Function(
                            transition = RelationalOrderLifecycleReferencingInnerValidWhile.States.Started.SubTransitions.ConfirmComplete.class,
                            value = Done.class)
                    static interface Delivering {}
                    @End
                    @ShortCut(RelationalOrderLifecycleReferencingInnerValidWhile.States.Finished.class)
                    static interface Done {}
                }
                @TransitionSet
                static interface SubTransitions {

                    static interface DoProduce {}
                    static interface DoDeliver {}
                    static interface ConfirmComplete {}
                }
                @RelationSet
                static interface Relations {

                    @RelateTo(ContractLifecycle.class)
                    static interface Contract {}
                }
            }
            @End
            static interface Finished {}
            @End
            static interface Canceled {}
        }
        @TransitionSet
        static interface Transitions {

            static interface Start {}
            static interface Cancel {}
        }
    }
    // ///////////////////////////////////////////////////////////////////////////////
    // Relational Case 3.
    // ///////////////////////////////////////////////////////////////////////////////
    @LifecycleMeta(RelationalOrderLifecycleReferencingInnerValidWhile.class)
    public static class ProductOrderInnerValidWhile extends ProductBase {

        private final Contract contract;

        public ProductOrderInnerValidWhile(Contract contract) {
            initialState(RelationalOrderLifecycleReferencingInnerValidWhile.States.Created.class.getSimpleName());
            this.contract = contract;
        }

        @Relation(RelationalOrderLifecycleReferencingInnerValidWhile.States.Started.Relations.Contract.class)
        public Contract getContract() {
            return contract;
        }

        @Transition
        public void start() {}

        @Transition
        public void cancel() {}

        @Transition
        public void doProduce() {}

        @Transition
        public void doDeliver() {}

        @Transition
        public void confirmComplete() {}
    }
}