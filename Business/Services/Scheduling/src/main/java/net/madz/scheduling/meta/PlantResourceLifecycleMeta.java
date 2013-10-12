package net.madz.scheduling.meta;

import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.scheduling.meta.PlantResourceLifecycleMeta.Transition.Maintain;

@StateMachine
public interface PlantResourceLifecycleMeta extends SchedulableResourceLifecycleMeta {

    @StateSet
    public static class States extends SchedulableResourceLifecycleMeta.States {

        @Function(transition = Maintain.class, value = { Maintaining.class })
        public static class Idle extends SchedulableResourceLifecycleMeta.States.Idle {}
        
        @Function(transition = Maintain.class, value = { Maintaining.class })
        public static class Busy extends SchedulableResourceLifecycleMeta.States.Busy {}

        public static class Maintaining {}
    }

    public static class Transition extends SchedulableResourceLifecycleMeta.Transitions {

        public static class Maintain {}
    }
}
