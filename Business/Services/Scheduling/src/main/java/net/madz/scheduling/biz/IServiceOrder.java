package net.madz.scheduling.biz;

import net.madz.core.biz.BOProxy;
import net.madz.core.biz.IBizObject;
import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.StateIndicator;
import net.madz.lifecycle.annotations.Transition;
import net.madz.scheduling.biz.impl.ServiceOrderBO;
import net.madz.scheduling.entities.ServiceOrder;
import net.madz.scheduling.meta.OrderLifecycleMeta.Transitions.Cancel;
import net.madz.scheduling.meta.OrderLifecycleMeta.Transitions.Finish;
import net.madz.scheduling.meta.OrderLifecycleMeta.Transitions.Start;
import net.madz.scheduling.meta.ServiceOrderLifecycleMeta;

@BOProxy(ServiceOrderBO.class)
@LifecycleMeta(value = ServiceOrderLifecycleMeta.class)
public interface IServiceOrder extends IPlantScheduleOrder, IVehicleScheduleOrder, IBizObject<ServiceOrder> {


    void configureResources(IServiceSummaryPlan summaryPlan, IMixingPlantResource plantResource, IConcreteTruckResource truckResource, double volume);

    /** Transition methods **/
    @Transition(Start.class)
    void confirmStart();

    @Transition(Finish.class)
    void confirmFinish();

    @Transition(Cancel.class)
    void cancel();

    @StateIndicator("serviceOrderState")
    String getState();
}
