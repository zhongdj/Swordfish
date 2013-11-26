package net.madz.scheduling.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.madz.contract.spec.entities.PouringPartSpec;
import net.madz.scheduling.meta.PlantScheduleOrderLifecycleMeta;
import net.madz.scheduling.meta.ServiceOrderLifecycleMeta;
import net.madz.scheduling.meta.VehicleScheduleOrderLifecycleMeta;

@MappedSuperclass
public class ServiceOrderBase extends OrderBase {

    private static final long serialVersionUID = 7027301156086367356L;
    @ManyToOne
    @JoinColumns({ @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false, referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "POURING_PART_SPEC_ID", nullable = false, insertable = true, updatable = false, referencedColumnName = "ID") })
    protected PouringPartSpec spec;
    @ManyToOne
    @JoinColumns({ @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false, referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "SUMMARY_PLAN_ID", nullable = false, insertable = true, updatable = false, referencedColumnName = "ID") })
    protected ServiceSummaryPlan summaryPlan;
    @ManyToOne
    @JoinColumns({ @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false, referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "MIXING_PLANT_RESOURCE_ID", nullable = false, insertable = true, updatable = true, referencedColumnName = "ID") })
    protected MixingPlantResource mixingPlantResource;
    @ManyToOne
    @JoinColumns({ @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false, referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "CONCRETE_TRUCK_RESOURCE_ID", nullable = false, insertable = true, updatable = true, referencedColumnName = "ID") })
    protected ConcreteTruckResource truckResource;
    @Column(name = "PLANNED_VOLUME", nullable = false, updatable = true)
    protected double plannedVolume;
    @Column(name = "TRANSPORT_FINSIHED_ON", nullable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date transportFinishedOn;
    @Column(name = "VEHICLE_LOADED_ON", nullable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date vehicleLoadOn;
    @Column(name = "VEHICLE_TRANSPORTED_ON", nullable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date vehicleTransportOn;
    @Column(name = "VEHICLE_CONSTRUCTED_ON", nullable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date vehicleConstructOn;
    @Column(name = "VEHICLE_COMPLETED_ON", nullable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date vehicleCompletedOn;
    @Column(name = "VEHICLE_ABORTED_ON", nullable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date vehicleAbortedOn;
    @Column(name = "VEHICLE_ORDER_STATE", nullable = false, updatable = true)
    private String vehicleScheduleOrderState = VehicleScheduleOrderLifecycleMeta.States.Ongoing.class.getSimpleName();
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PRODUCTION_FINISHED_ON", nullable = true, updatable = true)
    protected Date productionFinishedOn;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PLANT_STARTED_ON", nullable = true, updatable = true)
    protected Date plantStartedOn;
    @Column(name = "PLANT_FINISHED_ON", nullable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date plantFinishOn;
    @Column(name = "PLANT_ORDER_STATE", nullable = false, updatable = true)
    private String plantScheduleOrderState = PlantScheduleOrderLifecycleMeta.States.Created.class.getSimpleName();
    @Column(name = "SERVICE_ORDER_STATE", nullable = false, updatable = true)
    private String state = ServiceOrderLifecycleMeta.States.Draft.class.getSimpleName();
    @Column(name = "PLANT_CANCELED_ON", nullable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date plantCanceledOn;

    public ServiceOrderBase() {
        super();
    }

    public String getState() {
        return state;
    }

    @SuppressWarnings("unused")
    private void setState(String state) {
        this.state = state;
    }

    public PouringPartSpec getSpec() {
        return spec;
    }

    public ServiceSummaryPlan getSummaryPlan() {
        return summaryPlan;
    }

    public ConcreteTruckResource getTruckResource() {
        return truckResource;
    }

    public MixingPlantResource getMixingPlantResource() {
        return mixingPlantResource;
    }

    public double getPlannedVolume() {
        return plannedVolume;
    }

    @SuppressWarnings("unused")
    private void setVehicleScheduleOrderState(String state) {
        this.vehicleScheduleOrderState = state;
    }

    @SuppressWarnings("unused")
    private void setPlantScheduleOrderState(String state) {
        this.plantScheduleOrderState = state;
    }

    public String getVehicleScheduleOrderState() {
        return this.vehicleScheduleOrderState;
    }

    public String getPlantScheduleOrderState() {
        return this.plantScheduleOrderState;
    }
}