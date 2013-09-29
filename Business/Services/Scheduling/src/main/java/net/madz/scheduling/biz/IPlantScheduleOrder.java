package net.madz.scheduling.biz;

import java.util.Date;

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
import net.madz.lifecycle.annotations.state.ValidWhile;
import net.madz.scheduling.biz.IPlantScheduleOrder.States;
import net.madz.scheduling.biz.IPlantScheduleOrder.Transitions;
import net.madz.scheduling.biz.IPlantScheduleOrder.Transitions.Finish;
import net.madz.scheduling.biz.IPlantScheduleOrder.Transitions.Start;

@StateMachine(states = @StateSet(States.class), transitions = @TransitionSet(Transitions.class))
public interface IPlantScheduleOrder {

    @StateIndicator("plantScheduleOrderState")
    static class States {

        @Initial
        @InboundWhile(IServiceOrder.States.Scheduled.class)
        // Default ValidWhile(IServiceOrder.States.Scheduled.class)
        @Functions({ @Function(transition = Start.class, value = Working.class) })
        static class Created {}

        @InboundWhile(IServiceOrder.States.Ongoing.class)
        // Default ValidWhile(IServiceOrder.States.Ongoing.class)
        @Functions({ @Function(transition = Finish.class, value = Done.class) })
        static class Working {}

        @End
        @InboundWhile(IServiceOrder.States.Ongoing.class)
        @ValidWhile({ IServiceOrder.States.Ongoing.class, IServiceOrder.States.Finished.class })
        //Default @Functions({})
        static class Done {}
    }

    static class Transitions {

        static class Start {}

        static class Finish {}
    }

    @Transition(Transitions.Start.class)
    void doStartPlantOrder();

    @Transition(Transitions.Finish.class)
    void doFinishPlantOrder();

    /* NON-TRANSITION Methods */
    public String getPlantScheduleOrderState();

    String getPlantName();

    String getOperatorName();

    Date getProductionFinishedOn();

    Date getCreatedOn();

    String getCreatedBy();
}
