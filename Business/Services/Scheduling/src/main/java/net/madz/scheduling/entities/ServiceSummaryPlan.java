package net.madz.scheduling.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import net.madz.authorization.entities.MultiTenancyEntity;
import net.madz.contract.spec.entities.PouringPartSpec;

@Entity
@Table(name = "service_summary_plan")
public class ServiceSummaryPlan extends MultiTenancyEntity {

    private static final long serialVersionUID = -2519583821494066599L;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false,
                    referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "PRODUCE_SPEC_ID", nullable = false, insertable = true, updatable = false,
                    referencedColumnName = "ID") })
    private PouringPartSpec spec;

    @OneToMany(mappedBy="summaryPlan")
    private List<ServiceOrder> serviceOrderList = new ArrayList<>();

    @Column(name = "PLANNED_VOLUME")
    private double plannedVolume;

    @Column(name = "FINISHED")
    private boolean finished;

    public PouringPartSpec getSpec() {
        return spec;
    }

    public void setSpec(PouringPartSpec spec) {
        this.spec = spec;
    }

    public List<ServiceOrder> getServiceOrderList() {
        return serviceOrderList;
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

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void addResourceAllocatedTask(ServiceOrder resourceAllocatedTask) {
        if ( this.serviceOrderList.contains(resourceAllocatedTask) ) return;
        this.serviceOrderList.add(resourceAllocatedTask);
    }

    public void removeResourceAllocatedTask(ServiceOrder resourceAllocatedTask) {
        this.serviceOrderList.remove(resourceAllocatedTask);
    }
}
