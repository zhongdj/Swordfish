package net.madz.scheduling.entities;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import net.madz.authorization.entities.MultiTenancyEntity;
import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.StateIndicator;
import net.madz.lifecycle.annotations.Transition;
import net.madz.scheduling.biz.IVehicleScheduleOrder;
import net.madz.scheduling.meta.ConcreteTruckResourceLifecycleMeta;

@Entity
@Table(name = "concrete_truck_resource")
@LifecycleMeta(value = ConcreteTruckResourceLifecycleMeta.class)
public class ConcreteTruckResource extends MultiTenancyEntity {

    private static final long serialVersionUID = 1366415739718240376L;
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumns({ @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false, referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "CONCRETE_TRUCK_ID", nullable = false, insertable = true, updatable = false, referencedColumnName = "ID") })
    private ConcreteTruck concreteTruck;
    @OneToMany(targetEntity = ServiceOrder.class, mappedBy = "truckResource")
    private List<IVehicleScheduleOrder> incompleteScheduleOrders = new LinkedList<>();
    @Column(name = "Confirmed_Date")
    private Timestamp confirmedDate;
    @StateIndicator
    @Column(name = "STATE")
    private String state = ConcreteTruckResourceLifecycleMeta.States.Idle.class.getSimpleName();

    public ConcreteTruck getConcreteTruck() {
        return concreteTruck;
    }

    public void setConcreteTruck(ConcreteTruck concreteTruck) {
        this.concreteTruck = concreteTruck;
    }

    public List<IVehicleScheduleOrder> getScheduleOrdersQueue() {
        return incompleteScheduleOrders;
    }

    public Timestamp getConfirmedDate() {
        return confirmedDate;
    }

    public void setConfirmedDate(Timestamp confirmedDate) {
        this.confirmedDate = confirmedDate;
    }

    @Transition(ConcreteTruckResourceLifecycleMeta.Transitions.Assign.class)
    public void assignOrder(IVehicleScheduleOrder serviceOrder) {
        this.incompleteScheduleOrders.add(serviceOrder);
        //return incompleteScheduleOrders.size();
    }

    @Transition(ConcreteTruckResourceLifecycleMeta.Transitions.Release.class)
    public void finishOrder() {
        this.incompleteScheduleOrders.remove(0);
    }

    @Transition(ConcreteTruckResourceLifecycleMeta.Transitions.Detach.class)
    public void detach() {}

    @StateIndicator
    public String getState() {
        return this.state;
    }
}
