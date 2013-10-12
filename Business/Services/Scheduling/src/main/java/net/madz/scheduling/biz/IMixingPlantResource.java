package net.madz.scheduling.biz;

import net.madz.core.biz.BOProxy;
import net.madz.core.biz.IBizObject;
import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.StateIndicator;
import net.madz.lifecycle.annotations.Transition;
import net.madz.scheduling.biz.impl.MixingPlantResourceBO;
import net.madz.scheduling.entities.MixingPlantResource;
import net.madz.scheduling.meta.PlantResourceLifecycleMeta;

@BOProxy(MixingPlantResourceBO.class)
@LifecycleMeta(value = PlantResourceLifecycleMeta.class)
public interface IMixingPlantResource extends IBizObject<MixingPlantResource> {

    IPlantScheduleOrder getWorkingOrder();

    IPlantScheduleOrder[] getQueuedOrders();

    String getName();

    String getOperatorName();

    String getOperatorPhoneNumber();

    /** Transition methods **/
    @Transition(PlantResourceLifecycleMeta.Transitions.Assign.class)
    void assignOrder(IPlantScheduleOrder order);

    @Transition(PlantResourceLifecycleMeta.Transitions.Release.class)
    void release();

    @Transition(PlantResourceLifecycleMeta.Transitions.Maintain.class)
    void maintain();

    @StateIndicator
    String getState();
}
