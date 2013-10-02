package net.madz.lifecycle.demo.relational.meta;

import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.Functions;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.action.End;
import net.madz.lifecycle.annotations.state.InboundWhile;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.annotations.state.Parent;
import net.madz.lifecycle.annotations.state.RelationSet;
import net.madz.lifecycle.annotations.state.ValidWhile;
import net.madz.lifecycle.demo.relational.IServiceOrder;
import net.madz.lifecycle.demo.relational.meta.PlantScheduleOrderLifecycleMeta.Relations.ServiceOrder;
import net.madz.lifecycle.demo.relational.meta.PlantScheduleOrderLifecycleMeta.Transitions.Finish;
import net.madz.lifecycle.demo.relational.meta.PlantScheduleOrderLifecycleMeta.Transitions.Start;

@StateMachine(parentOn = ServiceableLifecycleMeta.class)
public interface PlantScheduleOrderLifecycleMeta {

    @StateSet
    static class States {

        @Initial
        @InboundWhile(relation = ServiceOrder.class, on = ServiceableLifecycleMeta.States.Scheduled.class)
        // Default @ValidWhile(relation="serviceOrder", on =
        // {IServiceOrder.States.Scheduled.class})
        @Functions({ @Function(transition = Start.class, value = Working.class) })
        static class Created {}

        @InboundWhile(relation = ServiceOrder.class, on = { ServiceableLifecycleMeta.States.Ongoing.class })
        // Default @ValidWhile(IServiceOrder.States.Ongoing.class)
        @Functions({ @Function(transition = Finish.class, value = Done.class) })
        static class Working {}

        @End
        @InboundWhile(relation = ServiceOrder.class, on = { ServiceableLifecycleMeta.States.Ongoing.class })
        @ValidWhile(relation = ServiceOrder.class, on = { ServiceableLifecycleMeta.States.Ongoing.class,
                ServiceableLifecycleMeta.States.Finished.class })
        // Default @Functions({})
        static class Done {}
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

    @Transition(Transitions.Start.class)
    void doStartPlantOrder();

    @Transition(Transitions.Finish.class)
    void doFinishPlantOrder();

    // @Parent
    IServiceOrder getServiceOrder();
}
