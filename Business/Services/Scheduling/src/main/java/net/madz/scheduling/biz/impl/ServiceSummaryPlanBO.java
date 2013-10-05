package net.madz.scheduling.biz.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import net.madz.common.entities.Additive;
import net.madz.common.entities.Address;
import net.madz.common.entities.Mixture;
import net.madz.core.biz.AbstractBO;
import net.madz.core.biz.BOFactory;
import net.madz.scheduling.biz.IServiceOrder;
import net.madz.scheduling.biz.IServiceSummaryPlan;
import net.madz.scheduling.entities.ServiceOrder;
import net.madz.scheduling.entities.ServiceSummaryPlan;

public class ServiceSummaryPlanBO extends AbstractBO<ServiceSummaryPlan> implements IServiceSummaryPlan {

    public ServiceSummaryPlanBO(EntityManager em, Class<ServiceSummaryPlan> t, long id) {
        super(em, t, id);
    }

    public ServiceSummaryPlanBO(ServiceSummaryPlan entity) {
        super(entity);
    }

    public ServiceSummaryPlanBO() {
        super(new ServiceSummaryPlan());
    }

    @Override
    public String getPouringPartName() {
        if ( !isPouringPartNull() ) {
            return null;
        }
        return this.entity.getSpec().getPouringPart().getName();
    }

    private boolean isPouringPartNull() {
        return null == this.entity.getSpec() || null == this.entity.getSpec().getPouringPart();
    }

    @Override
    public String getUnitProjectName() {
        if ( !isUnitProjectNull() ) {
            return null;
        }
        return this.entity.getSpec().getUnitProject().getName();
    }

    private boolean isUnitProjectNull() {
        return null == this.entity.getSpec() || null == this.entity.getSpec().getUnitProject();
    }

    @Override
    public Address getAddress() {
        if ( !isUnitProjectNull() ) {
            return null;
        }
        return this.entity.getSpec().getUnitProject().getAddress();
    }

    @Override
    public Mixture getMixture() {
        if ( !isSpecNull() ) {
            return null;
        }
        return this.entity.getSpec().getMixture();
    }

    private boolean isSpecNull() {
        return null == this.entity.getSpec();
    }

    @Override
    public List<Additive> getAdditives() {
        if ( !isSpecNull() ) {
            return new ArrayList<Additive>();
        }
        return this.entity.getSpec().getAdditives();
    }

    @Override
    public List<IServiceOrder> getServiceOrderList() {
        List<ServiceOrder> serviceOrders = this.entity.getServiceOrderList();
        List<IServiceOrder> results = new ArrayList<>();
        for ( ServiceOrder order : serviceOrders ) {
            final IServiceOrder serviceOrderBO = BOFactory.create(IServiceOrder.class, order);
            results.add(serviceOrderBO);
        }
        return results;
    }

    @Override
    public double getPlannedVolumn() {
        return this.entity.getPlannedVolume();
    }

    @Override
    public boolean getFinished() {
        return this.entity.isFinished();
    }
}
