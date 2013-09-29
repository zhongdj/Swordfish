package net.madz.scheduling.biz.impl;

import java.util.Date;

import javax.persistence.EntityManager;

import net.madz.common.entities.Address;
import net.madz.core.biz.AbstractBO;
import net.madz.customer.entities.Contact;
import net.madz.scheduling.biz.IConcreteTruckResource;
import net.madz.scheduling.biz.IMixingPlantResource;
import net.madz.scheduling.biz.IServiceOrder;
import net.madz.scheduling.biz.IVehicleScheduleOrder;
import net.madz.scheduling.entities.ServiceOrder;

public class ServiceOrderBO extends AbstractBO<ServiceOrder> implements IServiceOrder {

    public ServiceOrderBO(EntityManager em, long id) {
        super(em, ServiceOrder.class, id);
    }

    public ServiceOrderBO(ServiceOrder order) {
        super(order);
    }

    public ServiceOrderBO() {
        super(new ServiceOrder());
    }

    @Override
    public String getConcretePlantName() {
        if ( isMixingPlantNull() ) {
            return null;
        }
        return this.entity.getMixingPlantResource().getMixingPlant().getName();
    }

    private boolean isMixingPlantNull() {
        return null == this.entity || null == this.entity.getMixingPlantResource();
    }

    @Override
    public String getUnitProjectName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Address getAddress() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Contact getContact() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPouringPartName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getMixtureStrengthGrade() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String[] getAdditiveNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public double getVolume() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Date getCreatedOn() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getCreatedBy() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void doLoad() {
        // TODO Auto-generated method stub
    }

    @Override
    public void doTransport() {
        // TODO Auto-generated method stub
    }

    @Override
    public void doComplete() {
        // TODO Auto-generated method stub
    }

    @Override
    public void doAbort() {
        // TODO Auto-generated method stub
    }

    @Override
    public String getPlantScheduleOrderState() {
        return null;
    }

    @Override
    public IVehicleScheduleOrder.StateEnum getVehicleScheduleOrderState() {
        return null;
    }

    @Override
    public void allocateResources(IMixingPlantResource plantResource, IConcreteTruckResource truckResource,
            double volume) {
        plantResource.assignOrder(this);
        truckResource.assignOrder(this);
        this.entity.allocateResources(plantResource.get(), truckResource.get(), volume);
    }

    @Override
    public void doStartPlantOrder() {
        // TODO Auto-generated method stub
    }

    @Override
    public void doFinishPlantOrder() {
        // TODO Auto-generated method stub
    }

    @Override
    public String getPlantName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getOperatorName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getProductionFinishedOn() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getTransportFinishedOn() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void confirmStart() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void confirmFinish() {
        // TODO Auto-generated method stub
        
    }
}
