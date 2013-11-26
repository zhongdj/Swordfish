package net.madz.scheduling.entities;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import net.madz.authorization.entities.MultiTenancyEntity;
import net.madz.lifecycle.annotations.StateIndicator;
import net.madz.scheduling.biz.IVehicleScheduleOrder;
import net.madz.scheduling.meta.ConcreteTruckResourceLifecycleMeta;

@MappedSuperclass
public class ConcreteTruckResourceBase extends MultiTenancyEntity {

    private static final long serialVersionUID = 6476409986318373618L;
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumns({ @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false, referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "CONCRETE_TRUCK_ID", nullable = false, insertable = true, updatable = false, referencedColumnName = "ID") })
    private ConcreteTruck concreteTruck;
    @OneToMany(targetEntity = ServiceOrder.class, mappedBy = "truckResource")
    protected List<IVehicleScheduleOrder> incompleteScheduleOrders = new LinkedList<>();
    @Column(name = "Confirmed_Date")
    private Timestamp confirmedDate;
    @StateIndicator
    @Column(name = "STATE")
    private String state = ConcreteTruckResourceLifecycleMeta.States.Idle.class.getSimpleName();

    public ConcreteTruckResourceBase() {
        super();
    }

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

    @StateIndicator
    public String getState() {
        return this.state;
    }
}