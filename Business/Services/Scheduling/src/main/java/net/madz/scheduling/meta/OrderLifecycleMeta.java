package net.madz.scheduling.meta;

import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.Functions;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.scheduling.meta.OrderLifecycleMeta.Transitions.Cancel;
import net.madz.scheduling.meta.OrderLifecycleMeta.Transitions.Finish;
import net.madz.scheduling.meta.OrderLifecycleMeta.Transitions.Start;

@StateMachine
public interface OrderLifecycleMeta {

    @StateSet
    public static class States {

        @Initial
        @Functions(
                value = { @Function(transition = Start.class, value = { Ongoing.class }), @Function(transition = Cancel.class, value = { Cancelled.class }) })
        public static class Created {
        }

        @Functions(value = { @Function(transition = Finish.class, value = { Finished.class }),
                @Function(transition = Cancel.class, value = { Cancelled.class }) })
        public static class Ongoing {
        }

        @End
        public static class Finished {
        }

        @End
        public static class Cancelled {
        }
    }

    @TransitionSet
    public static class Transitions {

        public static class Start {
        }

        public static class Finish {
        }

        public static class Cancel {
        }
    }
}
