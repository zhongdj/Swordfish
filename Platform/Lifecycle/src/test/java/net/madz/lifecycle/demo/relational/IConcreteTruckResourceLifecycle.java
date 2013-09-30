package net.madz.lifecycle.demo.relational;

import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.Functions;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.action.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.demo.relational.IConcreteTruckResourceLifecycle.Transitions.Assign;
import net.madz.lifecycle.demo.relational.IConcreteTruckResourceLifecycle.Transitions.Detach;
import net.madz.lifecycle.demo.relational.IConcreteTruckResourceLifecycle.Transitions.Release;

@StateMachine
public interface IConcreteTruckResourceLifecycle {

    @StateSet
    // Default to @StateIndicator("state")
    static class States {

        @Initial
        @Functions({ @Function(transition = Assign.class, value = Busy.class),
                @Function(transition = Detach.class, value = Detached.class) })
        static class Idle {}

        @Function(transition = Release.class, value = Idle.class)
        static class Busy {}

        @End
        static class Detached {}
    }

    @TransitionSet
    public class Transitions {

        static class Assign {}

        static class Release {}

        static class Detach {}
    }

    @Transition
    // default to @Transition(Assign.class) use assign -> Assign
    void assign();

    @Transition(Release.class)
    void doRelease();

    @Transition
    // default to @Transition(Detach.class) use detach -> Detach
    void detach();
}
