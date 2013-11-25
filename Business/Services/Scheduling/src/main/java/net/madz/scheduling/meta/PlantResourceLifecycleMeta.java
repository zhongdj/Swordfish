package net.madz.scheduling.meta;

import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.scheduling.meta.PlantResourceLifecycleMeta.Transitions.ConfirmFixed;
import net.madz.scheduling.meta.PlantResourceLifecycleMeta.Transitions.Maintain;

@StateMachine
public interface PlantResourceLifecycleMeta extends SchedulableResourceLifecycleMeta {

    @StateSet
    public static class States extends SchedulableResourceLifecycleMeta.States {

        @Function(transition = Maintain.class, value = { Maintaining.class })
        public static class Idle extends SchedulableResourceLifecycleMeta.States.Idle {}
        
        @Function(transition = Maintain.class, value = { Maintaining.class })
        public static class Busy extends SchedulableResourceLifecycleMeta.States.Busy {}

        @Function(transition = ConfirmFixed.class, value = Idle.class)
        public static class Maintaining {}
    }

    @TransitionSet
    public static class Transitions extends SchedulableResourceLifecycleMeta.Transitions {

        public static class Maintain {}
        
        public static class ConfirmFixed {}
    }
}
