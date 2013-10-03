package net.madz.lifecycle.demo.inheritance.meta;

import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.action.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.demo.inheritance.meta.ConcreteTruckResourceLifecycleMeta.Transitions.Detach;

@StateMachine
public interface ConcreteTruckResourceLifecycleMeta extends SchedulableLifecycleMeta {

    @StateSet
    public static class States extends SchedulableLifecycleMeta.States {

        @Initial
        @Function(transition = Detach.class, value = Detached.class)
        public static class Idle extends SchedulableLifecycleMeta.States.Idle {}

        @End
        public static class Detached {}
    }

    @TransitionSet
    public class Transitions extends SchedulableLifecycleMeta.Transitions {

        public static class Detach {}
    }
}
