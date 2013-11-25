package net.madz.scheduling.biz;

import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.StateIndicator;
import net.madz.lifecycle.annotations.Transition;
import net.madz.scheduling.entities.ConcreteTruckResource;
import net.madz.scheduling.entities.MixingPlantResource;
import net.madz.scheduling.entities.ServiceSummaryPlan;
import net.madz.scheduling.meta.OrderLifecycleMeta.Transitions.Cancel;
import net.madz.scheduling.meta.OrderLifecycleMeta.Transitions.Finish;
import net.madz.scheduling.meta.ServiceOrderLifecycleMeta;

@LifecycleMeta(value = ServiceOrderLifecycleMeta.class)
public interface IServiceOrder {

    long getId();

    /** Transition methods **/
    @Transition(ServiceOrderLifecycleMeta.Transitions.Start.class)
    void configureResources(ServiceSummaryPlan summaryPlan, MixingPlantResource plantResource, ConcreteTruckResource truckResource, double volume);

    @Transition(Finish.class)
    void confirmFinish();

    @Transition(Cancel.class)
    void cancel();

    @StateIndicator("serviceOrderState")
    String getState();
}
