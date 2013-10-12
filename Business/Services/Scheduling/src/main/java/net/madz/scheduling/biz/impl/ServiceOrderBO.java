package net.madz.scheduling.biz.impl;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;

import net.madz.authorization.interceptor.UserSession;
import net.madz.common.entities.Additive;
import net.madz.common.entities.Address;
import net.madz.core.biz.AbstractBO;
import net.madz.customer.entities.Contact;
import net.madz.scheduling.biz.IConcreteTruckResource;
import net.madz.scheduling.biz.IMixingPlantResource;
import net.madz.scheduling.biz.IPlantScheduleOrder;
import net.madz.scheduling.biz.IServiceOrder;
import net.madz.scheduling.biz.IServiceSummaryPlan;
import net.madz.scheduling.entities.ServiceOrder;

public class ServiceOrderBO extends AbstractBO<ServiceOrder> implements IServiceOrder {

    private IMixingPlantResource plantResource;

    private IConcreteTruckResource truckResource;

    public ServiceOrderBO(EntityManager em, long id) {
        super(em, ServiceOrder.class, id);
    }

    public ServiceOrderBO(ServiceOrder order) {
        super(order);
    }

    public ServiceOrderBO() {
        super(new ServiceOrder());
        setStandardObjectProperties();
    }

    private void setStandardObjectProperties() {
        this.entity.setCreatedBy(UserSession.getUserSession().getUser());
        this.entity.setUpdatedBy(UserSession.getUserSession().getUser());
    }

    @Override
    public String getConcretePlantName() {
        if ( isMixingPlantNull() ) {
            return null;
        }
        return this.entity.getMixingPlantResource().getMixingPlant().getName();
    }

    private boolean isMixingPlantNull() {
        return null == this.entity.getMixingPlantResource();
    }

    @Override
    public String getUnitProjectName() {
        if ( isUnitProjectNull() ) {
            return null;
        }
        return this.entity.getSpec().getUnitProject().getName();
    }

    private boolean isUnitProjectNull() {
        return null == this.entity.getSpec() || null == this.entity.getSpec().getUnitProject();
    }

    @Override
    public Address getAddress() {
        if ( isUnitProjectNull() ) {
            return null;
        }
        return this.entity.getSpec().getUnitProject().getAddress();
    }

    @Override
    public Contact getContact() {
        if ( isUnitProjectNull() ) {
            return null;
        }
        return this.entity.getSpec().getUnitProject().getContact();
    }

    @Override
    public String getPouringPartName() {
        if ( isPouringPartNull() ) {
        }
        return this.entity.getSpec().getPouringPart().getName();
    }

    private boolean isPouringPartNull() {
        return null == this.entity.getSpec().getPouringPart();
    }

    @Override
    public String getMixtureStrengthGrade() {
        if ( isMixtureNull() ) {
            return null;
        }
        return this.entity.getSpec().getMixture().getGradeName();
    }

    private boolean isMixtureNull() {
        return null == this.entity.getSpec() || null == this.entity.getSpec().getMixture();
    }

    @Override
    public String[] getAdditiveNames() {
        if ( isAdditivesNull() ) {
            return new String[0];
        }
        final List<Additive> additives = this.entity.getSpec().getAdditives();
        final List<String> additiveNames = new LinkedList<String>();
        for ( Additive item : additives ) {
            additiveNames.add(item.getName());
        }
        return (String[]) additiveNames.toArray();
    }

    private boolean isAdditivesNull() {
        return null == this.entity.getSpec() || null == this.entity.getSpec().getAdditives();
    }

    @Override
    public double getVolume() {
        return this.entity.getPlannedVolume();
    }

    @Override
    public Date getCreatedOn() {
        return this.entity.getCreatedOn();
    }

    @Override
    public String getCreatedBy() {
        return this.entity.getCreatedBy().getUsername();
    }

    @Override
    public void configureResources(IServiceSummaryPlan summaryPlan, IMixingPlantResource plantResource, IConcreteTruckResource truckResource, double volume) {
        this.entity.setSummaryPlan(summaryPlan.get());
        this.entity.setSpec(summaryPlan.get().getSpec());
        this.plantResource = plantResource;
        this.truckResource = truckResource;
        this.plantResource.assignOrder(this);
        this.truckResource.assignOrder(this);
        this.entity.allocateResources(this.plantResource.get(), this.truckResource.get(), volume);
    }

    @Override
    public String getPlantName() {
        if ( isPlantNull() ) {
            return null;
        }
        return this.entity.getMixingPlantResource().getMixingPlant().getName();
    }

    private boolean isPlantNull() {
        return null == this.entity.getMixingPlantResource() || null == this.entity.getMixingPlantResource().getMixingPlant();
    }

    @Override
    public String getOperatorName() {
        return this.plantResource.getOperatorName();
    }

    @Override
    public Date getProductionFinishedOn() {
        return this.plantResource.getWorkingOrder().getProductionFinishedOn();
    }

    @Override
    public Date getTransportFinishedOn() {
        return this.truckResource.getWorkingOrder().getTransportFinishedOn();
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
    public void doLoad() {
        // TODO Auto-generated method stub
    }

    @Override
    public void doTransport() {
        // TODO Auto-generated method stub
    }

    @Override
    public void doConstruct() {
        // TODO Auto-generated method stub
    }

    @Override
    public void doComplete() {
        // TODO Auto-generated method stub
    }

    @Override
    public void doAbortOnVehicleScheduleOrder() {
        // TODO Auto-generated method stub
    }

    @Override
    public String getState() {
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

    @Override
    public void cancel() {
        // TODO Auto-generated method stub
    }

    @Override
    public String getPlantScheduleOrderState() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getVehicleScheduleOrderState() {
        // TODO Auto-generated method stub
        return null;
    }
}
