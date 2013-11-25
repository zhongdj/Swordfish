package net.madz.scheduling.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.madz.authorization.entities.MultiTenancyEntity;

@MappedSuperclass
public class OrderBase extends MultiTenancyEntity {

    private static final long serialVersionUID = 2716008526430133473L;
    @Column(name = "FINISHED_ON", nullable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date finishedOn;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CANCELED_ON", nullable = true, updatable = false)
    protected Date canceledOn;

    public OrderBase() {
        super();
    }

    public Date getFinishedOn() {
        return finishedOn;
    }

    public Date getCanceledOn() {
        return canceledOn;
    }
}