package net.madz.lifecycle.demo.inheritance.meta;

import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.Functions;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.annotations.state.StateOverride;
import net.madz.lifecycle.demo.inheritance.meta.PlantScheduleOrderLifecycleMeta.Transitions.Finish;
import net.madz.lifecycle.demo.inheritance.meta.VehicleScheduleOrderLifecycleMeta.Transitions.DoConstruct;
import net.madz.lifecycle.demo.inheritance.meta.VehicleScheduleOrderLifecycleMeta.Transitions.DoTransport;

@StateMachine
public interface VehicleScheduleOrderLifecycleMeta extends ServiceableLifecycleMeta {

    public static class States extends ServiceableLifecycleMeta.States {

        @StateOverride
        public static class Ongoing extends ServiceableLifecycleMeta.States.Ongoing {

            @Initial
            @Function(transition = DoTransport.class, value = OnPassage.class)
            public static class Loading {}

            @Functions({ @Function(transition = DoConstruct.class, value = Constructing.class) })
            public static class OnPassage {}

            @Function(transition = Finish.class, value = Finished.class)
            public static class Constructing {}
        }
    }

    public static class Transitions extends ServiceableLifecycleMeta.Transitions {

        public static class DoTransport {}

        public static class DoConstruct {}
    }
}
