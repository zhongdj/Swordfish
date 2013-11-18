package net.madz.scheduling.meta;

import net.madz.lifecycle.annotations.CompositeState;
import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.relation.RelationSet;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.annotations.state.ShortCut;
import net.madz.lifecycle.annotations.state.LifecycleOverride;
import net.madz.scheduling.meta.OrderLifecycleMeta.States.Finished;
import net.madz.scheduling.meta.OrderLifecycleMeta.Transitions.Finish;
import net.madz.scheduling.meta.VehicleScheduleOrderLifecycleMeta.Transitions.DoConstruct;
import net.madz.scheduling.meta.VehicleScheduleOrderLifecycleMeta.Transitions.DoTransport;

@StateMachine(parentOn = ServiceOrderLifecycleMeta.class)
public interface VehicleScheduleOrderLifecycleMeta extends OrderLifecycleMeta {

    @StateSet
    public static class States {

        @LifecycleOverride
        @CompositeState
        public static class Ongoing extends OrderLifecycleMeta.States.Ongoing {

            @Initial
            @Function(transition = DoTransport.class, value = { OnPassage.class })
            public static class Loading {}

            @Function(transition = DoConstruct.class, value = { Constructing.class })
            public static class OnPassage {}

            @Function(transition = Finish.class, value = { Exit.class })
            public static class Constructing {}

            @End
            @ShortCut(value = Finished.class)
            public static class Exit {}
        }
    }

    @TransitionSet
    public static class Transitions extends OrderLifecycleMeta.Transitions {

        public static class DoTransport {}

        public static class DoConstruct {}
    }

    @RelationSet
    public static class Relations {

        public static class ServiceOrder {}
    }
}
