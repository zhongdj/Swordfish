package net.madz.scheduling.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import net.madz.authorization.interceptor.UserSession;
import net.madz.common.entities.Additive;
import net.madz.common.entities.Address;
import net.madz.common.entities.Mixture;
import net.madz.contract.spec.entities.PouringPartSpec;
import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.StateIndicator;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.annotations.action.Condition;
import net.madz.scheduling.biz.IServiceOrder;
import net.madz.scheduling.meta.ServiceSummaryPlanLifecycleMeta;
import net.madz.scheduling.meta.ServiceSummaryPlanLifecycleMeta.Conditions.VolumeMeasurable;
import net.madz.scheduling.meta.ServiceSummaryPlanLifecycleMeta.Transitions.AdjustTotalVolume;
import net.madz.scheduling.meta.ServiceSummaryPlanLifecycleMeta.Transitions.CreateServiceOrder;

@Entity
@Table(name = "service_summary_plan")
@LifecycleMeta(ServiceSummaryPlanLifecycleMeta.class)
public class ServiceSummaryPlan extends OrderBase implements VolumeMeasurable {

    private static final long serialVersionUID = -2519583821494066599L;
    @ManyToOne
    @JoinColumns({ @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false, referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "PRODUCE_SPEC_ID", nullable = false, insertable = true, updatable = false, referencedColumnName = "ID") })
    private PouringPartSpec spec;
    @OneToMany(mappedBy = "summaryPlan")
    private List<ServiceOrder> serviceOrderList = new ArrayList<>();
    @Column(name = "TOTAL_VOLUME")
    private double totalVolume;
    @Column(name = "PLANNED_VOLUME")
    private double plannedVolume;
    @Column(name = "FINISHED_VOLUME")
    private double finishedVolume;
    @Column(name = "ONGOING_VOLUME")
    private double ongoingVolume;
    @Column(name = "CANCELLED_VOLUME")
    private double cancelledVolume;
    @Column(name = "STATE")
    @StateIndicator
    private String state = ServiceSummaryPlanLifecycleMeta.States.Ongoing.class.getSimpleName();

    public String getState() {
        return state;
    }

    public PouringPartSpec getSpec() {
        return spec;
    }

    public void setSpec(PouringPartSpec spec) {
        this.spec = spec;
    }

    public List<IServiceOrder> getServiceOrderList() {
        final ArrayList<IServiceOrder> result = new ArrayList<>();
        for ( ServiceOrder order : serviceOrderList ) {
            result.add(order);
        }
        return result;
    }

    public void setServiceOrderList(List<ServiceOrder> resourceAllocatedTasks) {
        this.serviceOrderList = resourceAllocatedTasks;
    }

    public double getPlannedVolume() {
        return plannedVolume;
    }

    public void setPlannedVolume(double plannedVolume) {
        this.plannedVolume = plannedVolume;
    }

    public void addResourceAllocatedTask(ServiceOrder resourceAllocatedTask) {
        if ( this.serviceOrderList.contains(resourceAllocatedTask) ) return;
        this.serviceOrderList.add(resourceAllocatedTask);
    }

    public void removeResourceAllocatedTask(ServiceOrder resourceAllocatedTask) {
        this.serviceOrderList.remove(resourceAllocatedTask);
    }

    public double getTotalVolume() {
        return totalVolume;
    }

    public double getFinishedVolume() {
        return finishedVolume;
    }

    public double getOngoingVolume() {
        return ongoingVolume;
    }

    public double getCancelledVolume() {
        return cancelledVolume;
    }

    public void setTotalVolume(double totalVolume) {
        this.totalVolume = totalVolume;
    }

    @Transition(CreateServiceOrder.class)
    public IServiceOrder createServiceOrder(MixingPlantResource plantResource, ConcreteTruckResource truckResource, double volume) {
        IServiceOrder order = createServiceOrder();
        order.configureResources(this, plantResource, truckResource, volume);
        return order;
    }

    private IServiceOrder createServiceOrder() {
        final ServiceOrder serviceOrder = new ServiceOrder();
        serviceOrder.setCreatedBy(UserSession.getUserSession().getUser());
        final Date time = new Date();
        serviceOrder.setCreatedOn(time);
        serviceOrder.setUpdatedBy(UserSession.getUserSession().getUser());
        serviceOrder.setUpdatedOn(time);
        return serviceOrder;
    }

    @Transition(AdjustTotalVolume.class)
    public int adjustTotalVolume(double newTotalVolume) {
        this.totalVolume = newTotalVolume;
        return 0;
    }

    @Transition(ServiceSummaryPlanLifecycleMeta.Transitions.ConfirmFinish.class)
    public float confirmFinish() {
        this.finishedOn = new Date();
        return 0;
    }

    @Transition(ServiceSummaryPlanLifecycleMeta.Transitions.Cancel.class)
    public double cancel() {
        this.canceledOn = new Date();
        return 0;
    }

    @Condition(VolumeMeasurable.class)
    public VolumeMeasurable getVolumeMeasurable() {
        return this;
    }

    public boolean isVolumeLeft() {
        final double leftValue = this.getTotalVolume() - this.getPlannedVolume();
        return leftValue > 0;
    }

    public String getPouringPartName() {
        return spec.getPouringPart().getName();
    }

    public String getUnitProjectName() {
        return spec.getUnitProject().getName();
    }

    public Address getAddress() {
        return spec.getUnitProject().getAddress();
    }

    public Mixture getMixture() {
        return spec.getMixture();
    }

    public List<Additive> getAdditives() {
        return Collections.unmodifiableList(spec.getAdditives());
    }
}
