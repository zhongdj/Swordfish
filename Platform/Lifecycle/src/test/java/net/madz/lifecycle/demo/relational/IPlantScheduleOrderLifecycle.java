package net.madz.lifecycle.demo.relational;

import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.Functions;
import net.madz.lifecycle.annotations.StateIndicator;
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
import net.madz.lifecycle.demo.relational.IPlantScheduleOrderLifecycle.Relations.ServiceOrder;
import net.madz.lifecycle.demo.relational.IPlantScheduleOrderLifecycle.Transitions.Finish;
import net.madz.lifecycle.demo.relational.IPlantScheduleOrderLifecycle.Transitions.Start;

@StateMachine(parentOn = IServiceOrderLifecycle.class)
public interface IPlantScheduleOrderLifecycle {

    @StateSet
    @StateIndicator("plantScheduleOrderState")
    static class States {

        @Initial
        @InboundWhile(relation = ServiceOrder.class, on = IServiceOrderLifecycle.States.Scheduled.class)
        // Default @ValidWhile(relation="serviceOrder", on =
        // {IServiceOrder.States.Scheduled.class})
        @Functions({ @Function(transition = Start.class, value = Working.class) })
        static class Created {}

        @InboundWhile(relation = ServiceOrder.class, on = { IServiceOrderLifecycle.States.Ongoing.class })
        // Default @ValidWhile(IServiceOrder.States.Ongoing.class)
        @Functions({ @Function(transition = Finish.class, value = Done.class) })
        static class Working {}

        @End
        @InboundWhile(relation = ServiceOrder.class, on = { IServiceOrderLifecycle.States.Ongoing.class })
        @ValidWhile(relation = ServiceOrder.class, on = { IServiceOrderLifecycle.States.Ongoing.class,
                IServiceOrderLifecycle.States.Finished.class })
        // Default @Functions({})
        static class Done {}
    }

    @TransitionSet
    static class Transitions {

        static class Start {}

        static class Finish {}
    }

    @RelationSet
    static class Relations {

        @Parent("serviceOrder") //Read parent from IPlantScheduleOrderLifecycle.getServiceOrder()
        //Or to use @Parent ONLY +  @Parent IServiceOrderLifecyle getServiceOrder();
        static class ServiceOrder {}
    }

    @Transition(Transitions.Start.class)
    void doStartPlantOrder();

    @Transition(Transitions.Finish.class)
    void doFinishPlantOrder();

    //@Parent
    IServiceOrderLifecycle getServiceOrder();
}
