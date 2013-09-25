package net.madz.scheduling.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import net.madz.authorization.entities.StandardObject;

@Entity
@Table(name = "concrete_truck_resource")
public class ConcreteTruckResource extends StandardObject {

    private static final long serialVersionUID = 1366415739718240376L;

    @OneToOne
    @JoinColumns({ @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "CONCRETE_TRUCK_ID", nullable = false, insertable = true, updatable = false) })
    private ConcreteTruck concreteTruck;

    private String state;

    public ConcreteTruck getConcreteTruck() {
        return concreteTruck;
    }

    public void setConcreteTruck(ConcreteTruck concreteTruck) {
        this.concreteTruck = concreteTruck;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
