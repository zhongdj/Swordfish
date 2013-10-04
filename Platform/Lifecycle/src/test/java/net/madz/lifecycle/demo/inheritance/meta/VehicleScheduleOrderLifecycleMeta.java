package net.madz.lifecycle.demo.inheritance.meta;

import net.madz.lifecycle.annotations.CompositeStateMachine;
import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.Functions;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.action.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.annotations.state.ShortCut;
import net.madz.lifecycle.annotations.state.StateOverride;
import net.madz.lifecycle.demo.inheritance.meta.PlantScheduleOrderLifecycleMeta.Transitions.Finish;
import net.madz.lifecycle.demo.inheritance.meta.VehicleScheduleOrderLifecycleMeta.Transitions.DoConstruct;
import net.madz.lifecycle.demo.inheritance.meta.VehicleScheduleOrderLifecycleMeta.Transitions.DoTransport;

@StateMachine
public interface VehicleScheduleOrderLifecycleMeta extends OrderLifecycleMeta {

    @StateSet
    public static class States extends OrderLifecycleMeta.States {

        @StateOverride
        @CompositeStateMachine
        public static class Ongoing extends OrderLifecycleMeta.States.Ongoing {

            @Initial
            @Function(transition = DoTransport.class, value = OnPassage.class)
            public static class Loading {}

            @Functions({ @Function(transition = DoConstruct.class, value = Constructing.class) })
            public static class OnPassage {}

            @Function(transition = Finish.class, value = Exit.class)
            public static class Constructing {}

            @End
            @ShortCut(Finished.class)
            public static class Exit {}
        }
    }

    @TransitionSet
    public static class Transitions extends OrderLifecycleMeta.Transitions {

        public static class DoTransport {}

        public static class DoConstruct {}
    }
}
