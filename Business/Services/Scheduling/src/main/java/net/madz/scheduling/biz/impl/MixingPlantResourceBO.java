package net.madz.scheduling.biz.impl;

import java.util.List;

import javax.persistence.EntityManager;

import net.madz.core.biz.AbstractBO;
import net.madz.core.utils.ProxyList;
import net.madz.scheduling.biz.IMixingPlantResource;
import net.madz.scheduling.biz.IPlantScheduleOrder;
import net.madz.scheduling.biz.IPlantScheduleOrder.StateEnum;
import net.madz.scheduling.entities.MixingPlantResource;

public class MixingPlantResourceBO extends AbstractBO<MixingPlantResource> implements IMixingPlantResource {

    private List<IPlantScheduleOrder> orderList;

    public MixingPlantResourceBO(EntityManager em, Class<MixingPlantResource> t, long id) {
        super(em, t, id);
        orderList = new ProxyList<>(IPlantScheduleOrder.class, this.entity.getLiveTasks());
    }

    public MixingPlantResourceBO(MixingPlantResource entity) {
        super(entity);
        orderList = new ProxyList<>(IPlantScheduleOrder.class, this.entity.getLiveTasks());
    }

    @Override
    public void assignOrder(IPlantScheduleOrder order) {
        this.orderList.add(order);
    }

    @Override
    public IPlantScheduleOrder getWorkingOrder() {
        for ( IPlantScheduleOrder order : orderList ) {
            StateEnum plantScheduleOrderState = order.getPlantScheduleOrderState();
            if ( plantScheduleOrderState == StateEnum.Working ) {
                return order;
            }
        }
        return null;
    }

    @Override
    public IPlantScheduleOrder[] getQueuedOrders() {
        return (IPlantScheduleOrder[]) this.orderList.toArray();
    }

    @Override
    public String getName() {
        return this.entity.getMixingPlant().getName();
    }

    @Override
    public String getOperatorName() {
        if ( null == this.entity.getMixingPlant().getOperator() ) {
            return null;
        }
        return this.entity.getMixingPlant().getOperator().getUsername();
    }

    @Override
    public String getOperatorPhoneNumber() {
        if ( null == this.entity.getMixingPlant().getOperator() ) {
            return null;
        }
        return this.entity.getMixingPlant().getOperator().getPhoneNumber();
    }
}
