package net.madz.scheduling.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.madz.common.entities.Additive;
import net.madz.common.entities.Address;
import net.madz.contract.spec.entities.PouringPartSpec;
import net.madz.customer.entities.Contact;
import net.madz.lifecycle.annotations.Transition;
import net.madz.scheduling.biz.IPlantScheduleOrder;
import net.madz.scheduling.biz.IServiceOrder;
import net.madz.scheduling.biz.IVehicleScheduleOrder;
import net.madz.scheduling.meta.PlantScheduleOrderLifecycleMeta;
import net.madz.scheduling.meta.ServiceOrderLifecycleMeta;
import net.madz.scheduling.meta.VehicleScheduleOrderLifecycleMeta;

@Entity
@Table(name = "service_order")
public class ServiceOrder extends OrderBase implements IServiceOrder, IPlantScheduleOrder, IVehicleScheduleOrder {

    private static final long serialVersionUID = -6118079224654228286L;
    @ManyToOne
    @JoinColumns({ @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false, referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "POURING_PART_SPEC_ID", nullable = false, insertable = true, updatable = false, referencedColumnName = "ID") })
    private PouringPartSpec spec;
    @ManyToOne
    @JoinColumns({ @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false, referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "SUMMARY_PLAN_ID", nullable = false, insertable = true, updatable = false, referencedColumnName = "ID") })
    private ServiceSummaryPlan summaryPlan;
    @ManyToOne
    @JoinColumns({ @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false, referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "MIXING_PLANT_RESOURCE_ID", nullable = false, insertable = true, updatable = true, referencedColumnName = "ID") })
    private MixingPlantResource mixingPlantResource;
    @ManyToOne
    @JoinColumns({ @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false, referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "CONCRETE_TRUCK_RESOURCE_ID", nullable = false, insertable = true, updatable = true, referencedColumnName = "ID") })
    private ConcreteTruckResource truckResource;
    @Column(name = "PLANNED_VOLUME", nullable = false, updatable = true)
    private double plannedVolume;
    @Column(name = "TRANSPORT_FINSIHED_ON", nullable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date transportFinishedOn;
    @Column(name = "VEHICLE_LOADED_ON", nullable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date vehicleLoadOn;
    @Column(name = "VEHICLE_TRANSPORTED_ON", nullable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date vehicleTransportOn;
    @Column(name = "VEHICLE_CONSTRUCTED_ON", nullable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date vehicleConstructOn;
    @Column(name = "VEHICLE_COMPLETED_ON", nullable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date vehicleCompletedOn;
    @Column(name = "VEHICLE_ABORTED_ON", nullable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date vehicleAbortedOn;
    @Column(name = "VEHICLE_ORDER_STATE", nullable = false, updatable = true)
    private String vehicleScheduleOrderState = VehicleScheduleOrderLifecycleMeta.States.Ongoing.class.getSimpleName();
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PRODUCTION_FINISHED_ON", nullable = true, updatable = true)
    private Date productionFinishedOn;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PLANT_STARTED_ON", nullable = true, updatable = true)
    private Date plantStartedOn;
    @Column(name = "PLANT_FINISHED_ON", nullable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date plantFinishOn;
    @Column(name = "PLANT_ORDER_STATE", nullable = false, updatable = true)
    private String plantScheduleOrderState = PlantScheduleOrderLifecycleMeta.States.Created.class.getSimpleName();
    @Column(name = "SERVICE_ORDER_STATE", nullable = false, updatable = true)
    private String state = ServiceOrderLifecycleMeta.States.Draft.class.getSimpleName();

    @Override
    public String getState() {
        return state;
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

    @Transition(ServiceOrderLifecycleMeta.Transitions.Start.class)
    public void configureResources(ServiceSummaryPlan serviceSummaryPlan, MixingPlantResource plantResource, ConcreteTruckResource truckResource, double volume) {
        {
            this.setSummaryPlan(serviceSummaryPlan);
            this.spec = summaryPlan.getSpec();
            this.truckResource = truckResource;
            this.mixingPlantResource = plantResource;
            this.plannedVolume = volume;
        }
        this.mixingPlantResource.assignOrder(this);
        this.truckResource.assignOrder(this);
    }

    @Transition(ServiceOrderLifecycleMeta.Transitions.Finish.class)
    public void confirmFinish() {
        this.finishedOn = new Date();
    }

    @Transition(ServiceOrderLifecycleMeta.Transitions.Cancel.class)
    public void cancel() {
        this.canceledOn = new Date();
    }

    private void setSummaryPlan(ServiceSummaryPlan summaryPlan) {
        if ( null != this.summaryPlan && null == summaryPlan ) {
            this.summaryPlan.removeResourceAllocatedTask(this);
        }
        this.summaryPlan = summaryPlan;
        this.summaryPlan.addResourceAllocatedTask(this);
    }

    @Override
    public String getConcretePlantName() {
        return this.mixingPlantResource.getMixingPlant().getName();
    }

    @Override
    public String getUnitProjectName() {
        return this.spec.getUnitProject().getName();
    }

    @Override
    public Address getAddress() {
        return this.spec.getUnitProject().getAddress();
    }

    @Override
    public Contact getContact() {
        return this.spec.getUnitProject().getContact();
    }

    @Override
    public String getPouringPartName() {
        return this.spec.getPouringPart().getName();
    }

    @Override
    public String getMixtureStrengthGrade() {
        return this.spec.getMixture().getGradeName();
    }

    @Override
    public String[] getAdditiveNames() {
        final ArrayList<String> additiveNames = new ArrayList<String>();
        final List<Additive> additives = this.spec.getAdditives();
        for ( Additive additive : additives ) {
            additiveNames.add(additive.getName());
        }
        return additiveNames.toArray(new String[0]);
    }

    @Override
    public double getTransportVolume() {
        return this.plannedVolume;
    }

    @Override
    public Date getTransportFinishedOn() {
        return this.transportFinishedOn;
    }

    @Override
    public void doLoad() {
        this.vehicleLoadOn = new Date();
    }

    @Override
    public void doTransport() {
        this.vehicleTransportOn = new Date();
    }

    @Override
    public void doConstruct() {
        this.vehicleConstructOn = new Date();
    }

    @Override
    public void doComplete() {
        this.vehicleCompletedOn = new Date();
    }

    @Override
    public void doAbortOnVehicleScheduleOrder() {
        this.vehicleAbortedOn = new Date();
    }

    @Override
    public String getVehicleScheduleOrderState() {
        return this.vehicleScheduleOrderState;
    }

    @Override
    public String getPlantName() {
        return this.mixingPlantResource.getMixingPlant().getName();
    }

    @Override
    public String getOperatorName() {
        return this.mixingPlantResource.getMixingPlant().getOperator().getFullName();
    }

    @Override
    public Date getProductionFinishedOn() {
        return this.productionFinishedOn;
    }

    @Override
    public void doStartPlantOrder() {
        this.plantStartedOn = new Date();
    }

    @Override
    public void doFinishPlantOrder() {
        this.plantFinishOn = new Date();
    }

    @Override
    public String getPlantScheduleOrderState() {
        return this.plantScheduleOrderState;
    }
}
