package net.madz.scheduling.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.madz.authorization.entities.MultiTenancyEntity;
import net.madz.contract.spec.entities.PouringPartSpec;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

@Entity
@Table(name = "service_order")
public class ServiceOrder extends MultiTenancyEntity {

    private static final long serialVersionUID = -6118079224654228286L;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false,
                    referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "POURING_PART_SPEC_ID", nullable = false, insertable = true, updatable = false,
                    referencedColumnName = "ID") })
    private PouringPartSpec spec;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false,
                    referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "SUMMARY_PLAN_ID", nullable = false, insertable = true, updatable = false,
                    referencedColumnName = "ID") })
    @XmlInverseReference(mappedBy = "resourceAllocatedTasks")
    private ServiceSummaryPlan summaryPlan;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false,
                    referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "MIXING_PLANT_RESOURCE_ID", nullable = false, insertable = true, updatable = true,
                    referencedColumnName = "ID") })
    private MixingPlantResource mixingPlantResource;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false,
                    referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "CONCRETE_TRUCK_RESOURCE_ID", nullable = false, insertable = true, updatable = true,
                    referencedColumnName = "ID") })
    private ConcreteTruckResource truckResource;

    @Column(name = "PLANNED_VOLUME", nullable = false, updatable = true)
    private double plannedVolume;

    private String state;

    public PouringPartSpec getSpec() {
        return spec;
    }

    public void setSpec(PouringPartSpec spec) {
        this.spec = spec;
    }

    public ServiceSummaryPlan getSummaryPlan() {
        return summaryPlan;
    }

    public void setSummaryPlan(ServiceSummaryPlan summaryPlan) {
        if ( null != this.summaryPlan && null == summaryPlan ) {
            this.summaryPlan.removeResourceAllocatedTask(this);
        }
        this.summaryPlan = summaryPlan;
        this.summaryPlan.addResourceAllocatedTask(this);
    }

    public ConcreteTruckResource getTruckResource() {
        return truckResource;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public MixingPlantResource getMixingPlantResource() {
        return mixingPlantResource;
    }

    public double getPlannedVolume() {
        return plannedVolume;
    }

    public void setPlannedVolume(double plannedVolume) {
        this.plannedVolume = plannedVolume;
    }

    public void allocateResources(MixingPlantResource mixingPlantResource, ConcreteTruckResource concreteTruckResource,
            double volume) {
        this.mixingPlantResource = mixingPlantResource;
        this.truckResource = concreteTruckResource;
        this.plannedVolume = volume;
    }
}
