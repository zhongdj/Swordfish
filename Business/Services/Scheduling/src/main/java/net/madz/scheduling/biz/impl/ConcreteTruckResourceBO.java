package net.madz.scheduling.biz.impl;

import java.util.List;

import javax.persistence.EntityManager;

import net.madz.core.biz.AbstractBO;
import net.madz.core.utils.ProxyList;
import net.madz.scheduling.biz.IConcreteTruckResource;
import net.madz.scheduling.biz.IVehicleScheduleOrder;
import net.madz.scheduling.entities.ConcreteTruckResource;
import net.madz.scheduling.entities.ServiceOrder;

public class ConcreteTruckResourceBO extends AbstractBO<ConcreteTruckResource> implements IConcreteTruckResource {

    private final List<IVehicleScheduleOrder> orderList;

    public ConcreteTruckResourceBO(ConcreteTruckResource entity) {
        super(entity);
        orderList = new ProxyList<ServiceOrder, IVehicleScheduleOrder>(IVehicleScheduleOrder.class, entity.getServiceOrders());
    }

    public ConcreteTruckResourceBO(EntityManager em, Class<ConcreteTruckResource> t, long id) {
        super(em, t, id);
        orderList = new ProxyList<ServiceOrder, IVehicleScheduleOrder>(IVehicleScheduleOrder.class, entity.getServiceOrders());
    }

    @Override
    public IVehicleScheduleOrder getWorkingOrder() {
        // TODO
        return null;
    }

    @Override
    public IVehicleScheduleOrder[] getQueuedOrder() {
        return (IVehicleScheduleOrder[]) this.orderList.toArray();
    }

    @Override
    public double getRatedCapacity() {
        return this.entity.getConcreteTruck().getRatedCapacity();
    }

    @Override
    public String getLicencePlateNumber() {
        if ( null == this.entity.getConcreteTruck() ) {
            return null;
        }
        return this.entity.getConcreteTruck().getLicencePlateNumber();
    }

    @Override
    public String getDriverName() {
        if ( null == this.entity.getConcreteTruck() ) {
            return null;
        }
        return this.entity.getConcreteTruck().getDriverName();
    }

    @Override
    public String getDriverPhoneNumber() {
        if ( null == this.entity.getConcreteTruck() ) {
            return null;
        }
        return this.entity.getConcreteTruck().getDriverPhoneNumber();
    }

    @Override
    public int assignOrder(IVehicleScheduleOrder order) {
        this.orderList.add(order);
        return this.orderList.size();
    }

    @Override
    public void finishOrder(Long orderId) {
        // TODO Auto-generated method stub
    }

    @Override
    public void detach() {
        // TODO Auto-generated method stub
    }

    @Override
    public String getState() {
        // TODO Auto-generated method stub
        return null;
    }
}
