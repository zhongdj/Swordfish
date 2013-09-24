package net.madz.scheduling.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import net.madz.authorization.entities.StandardObject;

@Entity
@Table(name = "concrete_truck")
public class ConcreteTruck extends StandardObject {

    private static final long serialVersionUID = -1132231092160514575L;

    @Column(name = "licence_plate_number", nullable = false)
    private String licencePlateNumber;

    @Column(name = "rated_capacity", nullable = false)
    private double ratedCapacity;

    public String getLicencePlateNumber() {
        return licencePlateNumber;
    }

    public void setLicencePlateNumber(String licencePlateNumber) {
        this.licencePlateNumber = licencePlateNumber;
    }

    public double getRatedCapacity() {
        return ratedCapacity;
    }

    public void setRatedCapacity(double ratedCapacity) {
        this.ratedCapacity = ratedCapacity;
    }
}
