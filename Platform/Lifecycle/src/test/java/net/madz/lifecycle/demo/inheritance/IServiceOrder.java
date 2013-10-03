package net.madz.lifecycle.demo.inheritance;

import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.StateIndicator;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.annotations.state.Relation;
import net.madz.lifecycle.demo.inheritance.meta.ServiceOrderLifecycleMeta.Relations.ConcreteTruckResource;
import net.madz.lifecycle.demo.inheritance.meta.ServiceOrderLifecycleMeta.Relations.PlantResource;
import net.madz.lifecycle.demo.inheritance.meta.ServiceableLifecycleMeta;
import net.madz.lifecycle.demo.inheritance.meta.ServiceableLifecycleMeta.Transitions.Schedule;

@LifecycleMeta(ServiceableLifecycleMeta.class)
public interface IServiceOrder {

    @Transition(Schedule.class)
    void allocateResources(@Relation(PlantResource.class) IPlantResource plantResource,
            @Relation(ConcreteTruckResource.class) IConcreteTruckResource truckResource);

    @Transition(ServiceableLifecycleMeta.Transitions.Start.class)
    void confirmStart();

    @Transition(ServiceableLifecycleMeta.Transitions.Finish.class)
    void confirmFinish();

    @StateIndicator
    String getServiceOrderState();
}