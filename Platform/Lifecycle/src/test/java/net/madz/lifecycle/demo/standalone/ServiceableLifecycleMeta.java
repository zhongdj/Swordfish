package net.madz.lifecycle.demo.standalone;

import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.Functions;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.action.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.demo.standalone.ServiceableLifecycleMeta.Transitions.Cancel;
import net.madz.lifecycle.demo.standalone.ServiceableLifecycleMeta.Transitions.Finish;
import net.madz.lifecycle.demo.standalone.ServiceableLifecycleMeta.Transitions.Schedule;
import net.madz.lifecycle.demo.standalone.ServiceableLifecycleMeta.Transitions.Start;

/**
 * There won't be any implementation of this interface, since this interface is
 * ONLY meta data of serviceable life cycle.
 * 
 * @author Barry
 * 
 */
@StateMachine
public interface ServiceableLifecycleMeta {

    @StateSet
    static class States {

        @Initial
        @Function(transition = Schedule.class, value = Queued.class)
        static class Created {}

        @Functions({ @Function(transition = Start.class, value = Ongoing.class),
                @Function(transition = Cancel.class, value = Cancelled.class) })
        static class Queued {}

        @Functions({ @Function(transition = Finish.class, value = Finished.class),
                @Function(transition = Cancel.class, value = Cancelled.class) })
        static class Ongoing {}

        @End
        static class Finished {}

        @End
        static class Cancelled {}
    }

    @TransitionSet
    static class Transitions {

        static class Schedule {}

        static class Start {}

        static class Finish {}

        static class Cancel {}
    }
}