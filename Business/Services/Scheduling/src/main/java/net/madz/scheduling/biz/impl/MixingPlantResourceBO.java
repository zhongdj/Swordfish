package net.madz.scheduling.biz.impl;

import javax.persistence.EntityManager;

import net.madz.core.biz.AbstractBO;
import net.madz.scheduling.biz.IMixingPlantResource;
import net.madz.scheduling.biz.IPlantScheduleOrder;
import net.madz.scheduling.entities.MixingPlantResource;

public class MixingPlantResourceBO extends AbstractBO<MixingPlantResource> implements IMixingPlantResource {

    public MixingPlantResourceBO(EntityManager em, Class<MixingPlantResource> t, long id) {
        super(em, t, id);
    }

    public MixingPlantResourceBO(MixingPlantResource entity) {
        super(entity);
    }

    @Override
    public void assignOrder(IPlantScheduleOrder order) {
        // TODO Auto-generated method stub
    }

    @Override
    public IPlantScheduleOrder getWorkingOrder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IPlantScheduleOrder[] getQueuedOrders() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getOperatorName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getOperatorPhoneNumber() {
        // TODO Auto-generated method stub
        return null;
    }
}
