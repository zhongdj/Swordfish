package net.madz.scheduling.biz;

import java.util.Date;

import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.StateIndicator;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.annotations.relation.Relation;
import net.madz.scheduling.meta.PlantScheduleOrderLifecycleMeta;

@LifecycleMeta(value = PlantScheduleOrderLifecycleMeta.class)
public interface IPlantScheduleOrder {

    long getId();

    /* NON-TRANSITION Methods */
    String getPlantName();

    String getOperatorName();

    Date getProductionFinishedOn();

    Date getCreatedOn();

    /** Transition methods **/
    @Transition(PlantScheduleOrderLifecycleMeta.Transitions.Start.class)
    void doStartPlantOrder();

    @Transition(PlantScheduleOrderLifecycleMeta.Transitions.Finish.class)
    void doFinishPlantOrder();

    @Transition(PlantScheduleOrderLifecycleMeta.Transitions.Cancel.class)
    void cancelPlantOrder();

    @StateIndicator
    String getPlantScheduleOrderState();
    
    @Relation(PlantScheduleOrderLifecycleMeta.Relations.ServiceOrder.class)
    IServiceOrder getServiceOrder();
}
