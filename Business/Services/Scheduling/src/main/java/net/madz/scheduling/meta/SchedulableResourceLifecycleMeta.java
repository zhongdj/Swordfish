package net.madz.scheduling.meta;

import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.scheduling.meta.SchedulableResourceLifecycleMeta.Transitions.Assign;
import net.madz.scheduling.meta.SchedulableResourceLifecycleMeta.Transitions.Release;

@StateMachine
public interface SchedulableResourceLifecycleMeta {

    @StateSet
    public static abstract class States {
        @Initial
        @Function(transition = Assign.class, value = { Busy.class })
        public static class Idle {
        }
        @Function(transition = Release.class, value = { Idle.class })
        public static class Busy {
        }
    }

    @TransitionSet
    public static class Transitions {

        public static class Assign {
        }
        
        public static class Release {
            
        }
    }
}