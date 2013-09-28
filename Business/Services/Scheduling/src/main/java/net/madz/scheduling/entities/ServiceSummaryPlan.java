package net.madz.scheduling.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import net.madz.authorization.entities.StandardObject;
import net.madz.contract.spec.entities.PouringPartSpec;

@Entity
@Table(name = "planned_summary_task")
public class ServiceSummaryPlan extends StandardObject {

    private static final long serialVersionUID = -2519583821494066599L;

    @ManyToOne
    @Column(name = "PRODUCE_SPEC_ID", nullable = false)
    private PouringPartSpec spec;

    @OneToMany
    private List<ServiceOrder> resourceAllocatedTasks = new ArrayList<>();

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

    public List<ServiceOrder> getResourceAllocatedTasks() {
        return resourceAllocatedTasks;
    }

    public void setResourceAllocatedTasks(List<ServiceOrder> resourceAllocatedTasks) {
        this.resourceAllocatedTasks = resourceAllocatedTasks;
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
        if ( this.resourceAllocatedTasks.contains(resourceAllocatedTask) ) return;
        this.resourceAllocatedTasks.add(resourceAllocatedTask);
    }

    public void removeResourceAllocatedTask(ServiceOrder resourceAllocatedTask) {
        this.resourceAllocatedTasks.remove(resourceAllocatedTask);
    }
}
