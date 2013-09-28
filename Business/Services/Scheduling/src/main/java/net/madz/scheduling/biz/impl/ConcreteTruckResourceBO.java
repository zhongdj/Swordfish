package net.madz.scheduling.biz.impl;

import javax.persistence.EntityManager;

import net.madz.core.biz.AbstractBO;
import net.madz.scheduling.biz.IConcreteTruckResource;
import net.madz.scheduling.biz.IVehicleScheduleOrder;
import net.madz.scheduling.entities.ConcreteTruckResource;

public class ConcreteTruckResourceBO extends AbstractBO<ConcreteTruckResource> implements IConcreteTruckResource {

    public ConcreteTruckResourceBO(ConcreteTruckResource entity) {
        super(entity);
    }

    public ConcreteTruckResourceBO(EntityManager em, Class<ConcreteTruckResource> t, long id) {
        super(em, t, id);
    }

    @Override
    public int assignOrder(IVehicleScheduleOrder order) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void confirmReady() {
        // TODO Auto-generated method stub
    }

    @Override
    public void confirmDetachPlant() {
        // TODO Auto-generated method stub
    }

    @Override
    public void confirmLeaveStation() {
        // TODO Auto-generated method stub
    }

    @Override
    public void confirmArriveToConstructionPlant() {
        // TODO Auto-generated method stub
    }

    @Override
    public void confirmReturn() {
        // TODO Auto-generated method stub
    }

    @Override
    public void confirmArriveStation() {
        // TODO Auto-generated method stub
    }

    @Override
    public StateEnum getState() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IVehicleScheduleOrder getWorkingOrder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IVehicleScheduleOrder[] getQueuedOrder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public double getRatedCapacity() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getLicencePlateNumber() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDriverName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDriverPhoneNumber() {
        // TODO Auto-generated method stub
        return null;
    }
}
