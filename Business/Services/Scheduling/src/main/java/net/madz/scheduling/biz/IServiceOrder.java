package net.madz.scheduling.biz;

import net.madz.core.biz.BOProxy;
import net.madz.core.biz.IBizObject;
import net.madz.lifecycle.annotations.StateIndicator;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.annotations.action.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.scheduling.biz.IServiceOrder.Transitions.Finish;
import net.madz.scheduling.biz.IServiceOrder.Transitions.Schedule;
import net.madz.scheduling.biz.IServiceOrder.Transitions.Start;
import net.madz.scheduling.biz.impl.ServiceOrderBO;
import net.madz.scheduling.entities.ServiceOrder;

@BOProxy(ServiceOrderBO.class)
//@StateMachine(states = @StateSet(IServiceOrder.States.class),
//        transitions = @TransitionSet(IServiceOrder.Transitions.class))
public interface IServiceOrder extends IPlantScheduleOrder, IVehicleScheduleOrder, IBizObject<ServiceOrder> {

    @StateIndicator("serviceOrderState")
    static class States {

        @Initial
        static class Created {}

        static class Scheduled {}

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
    void allocateResources(IMixingPlantResource plantResource, IConcreteTruckResource truckResource, double volume);

    @Transition(Start.class)
    void confirmStart();

    @Transition(Finish.class)
    void confirmFinish();
}
