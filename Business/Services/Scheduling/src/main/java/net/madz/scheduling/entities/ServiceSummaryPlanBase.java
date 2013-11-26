package net.madz.scheduling.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import net.madz.common.entities.Additive;
import net.madz.common.entities.Address;
import net.madz.common.entities.Mixture;
import net.madz.contract.spec.entities.PouringPartSpec;
import net.madz.lifecycle.annotations.StateIndicator;
import net.madz.scheduling.biz.IServiceOrder;
import net.madz.scheduling.meta.ServiceSummaryPlanLifecycleMeta;

@MappedSuperclass
public class ServiceSummaryPlanBase extends OrderBase {

    private static final long serialVersionUID = 8018907849365962171L;
    @ManyToOne
    @JoinColumns({ @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false, referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "PRODUCE_SPEC_ID", nullable = false, insertable = true, updatable = false, referencedColumnName = "ID") })
    protected PouringPartSpec spec;
    @OneToMany(mappedBy = "summaryPlan")
    protected List<ServiceOrder> serviceOrderList = new ArrayList<>();
    @Column(name = "TOTAL_VOLUME")
    protected double totalVolume;
    @Column(name = "PLANNED_VOLUME")
    protected double plannedVolume;
    @Column(name = "FINISHED_VOLUME")
    protected double finishedVolume;
    @Column(name = "ONGOING_VOLUME")
    protected double ongoingVolume;
    @Column(name = "CANCELLED_VOLUME")
    protected double cancelledVolume;
    @Column(name = "STATE")
    @StateIndicator
    private String state = ServiceSummaryPlanLifecycleMeta.States.Ongoing.class.getSimpleName();

    public ServiceSummaryPlanBase() {
        super();
    }

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