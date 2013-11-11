package net.madz.lifecycle.engine;

import net.madz.lifecycle.annotations.CompositeStateMachine;
import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.StateIndicator;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.annotations.TransitionSet;
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

    @StateMachine
    static interface BaseLifecycle {

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
                    @Function(transition = BaseLifecycle.States.Started.SubTransitions.DoProduce.class,
                            value = Producing.class)
                    static interface OrderCreated {}
                    @Function(transition = BaseLifecycle.States.Started.SubTransitions.DoDeliver.class,
                            value = Delivering.class)
                    static interface Producing {}
                    @Function(transition = BaseLifecycle.States.Started.SubTransitions.ConfirmComplete.class,
                            value = Done.class)
                    static interface Delivering {}
                    @End
                    @ShortCut(BaseLifecycle.States.Finished.class)
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
    @LifecycleMeta(BaseLifecycle.class)
    public static class Product extends ReactiveObject {

        public Product() {
            initialState(BaseLifecycle.States.Created.class.getSimpleName());
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
