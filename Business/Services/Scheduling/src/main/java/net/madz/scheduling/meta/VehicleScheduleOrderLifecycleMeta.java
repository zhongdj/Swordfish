package net.madz.scheduling.meta;

import net.madz.lifecycle.annotations.CompositeState;
import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.relation.ErrorMessage;
import net.madz.lifecycle.annotations.relation.InboundWhile;
import net.madz.lifecycle.annotations.relation.Parent;
import net.madz.lifecycle.annotations.relation.RelateTo;
import net.madz.lifecycle.annotations.relation.RelationSet;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.annotations.state.LifecycleOverride;
import net.madz.lifecycle.annotations.state.ShortCut;
import net.madz.scheduling.meta.OrderLifecycleMeta.States.Finished;
import net.madz.scheduling.sessions.Consts;
import net.madz.scheduling.sessions.Consts.ErrorCodes;

@StateMachine
public interface VehicleScheduleOrderLifecycleMeta extends OrderLifecycleMeta {

    @StateSet
    public static class States {

        @LifecycleOverride
        @CompositeState
        @InboundWhile(relation = Relations.ConcreteTruckResource.class, on = { ConcreteTruckResourceLifecycleMeta.States.Idle.class },
                otherwise = { @ErrorMessage(bundle = Consts.BUNDLE_NAME, code = ErrorCodes.TRUCK_RESOURCE_NOT_IN_IDLE_OR_BUSY_STATE,
                        states = { ConcreteTruckResourceLifecycleMeta.States.Detached.class }) })
        public static class Ongoing extends OrderLifecycleMeta.States.Ongoing {

            @StateSet
            public static class CStates {

                @Initial
                @Function(transition = CTransitions.DoTransport.class, value = { OnPassage.class })
                public static class Loading {}
                @Function(transition = CTransitions.DoConstruct.class, value = { Constructing.class })
                public static class OnPassage {}
                @Function(transition = CTransitions.DoFinish.class, value = { Exit.class })
                public static class Constructing {}
                @End
                @ShortCut(value = Finished.class)
                public static class Exit {}
            }
            @TransitionSet
            public static class CTransitions {

                public static class DoFinish {}
                public static class DoTransport {}
                public static class DoConstruct {}
            }
        }
    }
    @TransitionSet
    public static class Transitions extends OrderLifecycleMeta.Transitions {}
    @RelationSet
    public static class Relations {

        @Parent
        @RelateTo(ServiceOrderLifecycleMeta.class)
        public static class ServiceOrder {}
        @RelateTo(ConcreteTruckResourceLifecycleMeta.class)
        public static class ConcreteTruckResource {}
    }
}
