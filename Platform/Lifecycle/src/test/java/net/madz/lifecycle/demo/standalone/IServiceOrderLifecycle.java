package net.madz.lifecycle.demo.standalone;

import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.StateIndicator;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.action.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.demo.standalone.IServiceOrderLifecycle.Transitions.Finish;
import net.madz.lifecycle.demo.standalone.IServiceOrderLifecycle.Transitions.Schedule;
import net.madz.lifecycle.demo.standalone.IServiceOrderLifecycle.Transitions.Start;

@StateMachine(states = @StateSet(IServiceOrderLifecycle.States.class),
        transitions = @TransitionSet(IServiceOrderLifecycle.Transitions.class))
public interface IServiceOrderLifecycle {

    @StateIndicator("serviceOrderState")
    static class States {

        @Initial
        @Function(transition = Schedule.class, value = Scheduled.class)
        static class Created {}

        @Function(transition = Start.class, value = Ongoing.class)
        static class Scheduled {}

        @Function(transition = Finish.class, value = Finished.class)
        static class Ongoing {}

        @End
        static class Finished {}
    }

    static class Transitions {

        static class Schedule {}

        static class Start {}

        static class Finish {}
    }

    @Transition(Schedule.class)
    void allocateResources();

    @Transition(Start.class)
    void confirmStart();

    @Transition(Finish.class)
    void confirmFinish();
}