package net.madz.scheduling.meta;

import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.state.End;
import net.madz.scheduling.meta.ConcreteTruckResourceLifecycleMeta.Transitions.Detach;

@StateMachine
public interface ConcreteTruckResourceLifecycleMeta extends SchedulableResourceLifecycleMeta {

    @StateSet
    public static class States extends SchedulableResourceLifecycleMeta.States {

        @Function(transition = Detach.class, value = { Detached.class })
        public static class Idle extends SchedulableResourceLifecycleMeta.States.Idle {}

        @End
        public static class Detached {}
    }

    public static class Transitions extends SchedulableResourceLifecycleMeta.Transitions {

        public static class Detach {}
    }
}
