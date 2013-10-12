package net.madz.scheduling.biz;

import java.util.Date;

import net.madz.core.biz.BOProxy;
import net.madz.core.biz.IBizObject;
import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.StateIndicator;
import net.madz.lifecycle.annotations.Transition;
import net.madz.scheduling.biz.impl.ServiceOrderBO;
import net.madz.scheduling.entities.ServiceOrder;
import net.madz.scheduling.meta.PlantScheduleOrderLifecycleMeta;

//@StateMachine(states = @StateSet(States.class), transitions = @TransitionSet(Transitions.class))
@BOProxy(ServiceOrderBO.class)
@LifecycleMeta(value = PlantScheduleOrderLifecycleMeta.class)
public interface IPlantScheduleOrder extends IBizObject<ServiceOrder> {

    /* NON-TRANSITION Methods */
    String getPlantName();

    String getOperatorName();

    Date getProductionFinishedOn();

    Date getCreatedOn();

    String getCreatedBy();

    /** Transition methods **/
    @Transition(PlantScheduleOrderLifecycleMeta.Transitions.Start.class)
    void doStartPlantOrder();

    @Transition(PlantScheduleOrderLifecycleMeta.Transitions.Finish.class)
    void doFinishPlantOrder();

    @StateIndicator
    String getPlantScheduleOrderState();
}
