package net.madz.scheduling.entities;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

import net.madz.authorization.entities.MultiTenancyEntity;

@Entity
@Table(name = "concrete_truck_resource")
public class ConcreteTruckResource extends MultiTenancyEntity {

    private static final long serialVersionUID = 1366415739718240376L;
    @OneToOne
    @JoinColumns({ @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false, referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "CONCRETE_TRUCK_ID", nullable = false, insertable = true, updatable = false, referencedColumnName = "ID") })
    private ConcreteTruck concreteTruck;
    @OneToMany(mappedBy = "truckResource")
    @XmlInverseReference(mappedBy = "truckResource")
    private List<ServiceOrder> serviceOrders = new LinkedList<ServiceOrder>();
    private String state;
    @Column(name = "Confirmed_Date")
    private Timestamp confirmedDate;

    public ConcreteTruck getConcreteTruck() {
        return concreteTruck;
    }

    public void setConcreteTruck(ConcreteTruck concreteTruck) {
        this.concreteTruck = concreteTruck;
    }

    public String getState() {
        return state;
    }

    private void setState(String state) {
        this.state = state;
    }

    public List<ServiceOrder> getServiceOrders() {
        return serviceOrders;
    }

    public void setServiceOrders(List<ServiceOrder> serviceOrders) {
        this.serviceOrders = serviceOrders;
    }

    public Timestamp getConfirmedDate() {
        return confirmedDate;
    }

    public void setConfirmedDate(Timestamp confirmedDate) {
        this.confirmedDate = confirmedDate;
    }
}
