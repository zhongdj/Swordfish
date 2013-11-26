package net.madz.scheduling.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.Transition;
import net.madz.scheduling.biz.IVehicleScheduleOrder;
import net.madz.scheduling.meta.ConcreteTruckResourceLifecycleMeta;

@Entity
@Table(name = "concrete_truck_resource")
@LifecycleMeta(value = ConcreteTruckResourceLifecycleMeta.class)
public class ConcreteTruckResource extends ConcreteTruckResourceBase {

    private static final long serialVersionUID = 1366415739718240376L;

    @Transition(ConcreteTruckResourceLifecycleMeta.Transitions.Assign.class)
    public int assignOrder(IVehicleScheduleOrder serviceOrder) {
        this.incompleteScheduleOrders.add(serviceOrder);
        return incompleteScheduleOrders.size();
    }

    @Transition(ConcreteTruckResourceLifecycleMeta.Transitions.Release.class)
    public void finishOrder() {
        this.incompleteScheduleOrders.remove(0);
    }

    @Transition(ConcreteTruckResourceLifecycleMeta.Transitions.Detach.class)
    public void detach() {}
}
