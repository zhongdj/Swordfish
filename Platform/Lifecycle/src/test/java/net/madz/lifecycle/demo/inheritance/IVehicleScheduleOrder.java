package net.madz.lifecycle.demo.inheritance;

import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.demo.inheritance.meta.VehicleScheduleOrderLifecycleMeta;

@LifecycleMeta(VehicleScheduleOrderLifecycleMeta.class)
public interface IVehicleScheduleOrder {

    @Transition(VehicleScheduleOrderLifecycleMeta.Transitions.Start.class)
    void doLoad();

    @Transition(VehicleScheduleOrderLifecycleMeta.Transitions.DoTransport.class)
    void doTransport();

    @Transition(VehicleScheduleOrderLifecycleMeta.Transitions.DoConstruct.class)
    void doConstruct();
}
