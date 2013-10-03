package net.madz.lifecycle.demo.inheritance.meta;

import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.Functions;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.action.End;
import net.madz.lifecycle.annotations.state.InboundWhile;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.annotations.state.RelationSet;
import net.madz.lifecycle.annotations.state.ValidWhile;
import net.madz.lifecycle.demo.inheritance.meta.PlantScheduleOrderLifecycleMeta.Relations.ServiceOrder;
import net.madz.lifecycle.demo.inheritance.meta.PlantScheduleOrderLifecycleMeta.Transitions.Finish;
import net.madz.lifecycle.demo.inheritance.meta.PlantScheduleOrderLifecycleMeta.Transitions.Start;

@StateMachine(parentOn = ServiceOrderLifecycleMeta.class)
public interface PlantScheduleOrderLifecycleMeta extends ServiceableLifecycleMeta {

    @StateSet
    static class States extends ServiceableLifecycleMeta.States {

        @Initial
        @InboundWhile(relation = ServiceOrder.class, on = ServiceableLifecycleMeta.States.Queued.class)
        // Default @ValidWhile(relation="serviceOrder", on =
        // {ServiceOrderLifecycleMeta.States.Queued.class})
        @Functions({ @Function(transition = Start.class, value = Ongoing.class) })
        static class Created extends ServiceableLifecycleMeta.States.Created {}

        @InboundWhile(relation = ServiceOrder.class, on = { ServiceOrderLifecycleMeta.States.Ongoing.class })
        // Default @ValidWhile(IServiceOrder.States.Ongoing.class)
        @Functions({ @Function(transition = Finish.class, value = Finished.class) })
        static class Ongoing extends ServiceableLifecycleMeta.States.Ongoing {}

        @End
        @InboundWhile(relation = ServiceOrder.class, on = { ServiceOrderLifecycleMeta.States.Ongoing.class })
        @ValidWhile(relation = ServiceOrder.class, on = { ServiceOrderLifecycleMeta.States.Ongoing.class,
                ServiceOrderLifecycleMeta.States.Finished.class })
        // Default @Functions({})
        static class Finished extends ServiceableLifecycleMeta.States.Finished {}
    }

    @TransitionSet
    public static class Transitions {

        public static class Start {}

        public static class Finish {}
    }

    @RelationSet
    public static class Relations {

        public static class ServiceOrder {}
    }
}
