package net.madz.scheduling.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

import net.madz.common.entities.Additive;
import net.madz.common.entities.Address;
import net.madz.customer.entities.Contact;
import net.madz.lifecycle.annotations.ReactiveObject;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.annotations.relation.Relation;
import net.madz.scheduling.biz.IPlantScheduleOrder;
import net.madz.scheduling.biz.IServiceOrder;
import net.madz.scheduling.biz.IVehicleScheduleOrder;
import net.madz.scheduling.meta.PlantScheduleOrderLifecycleMeta;
import net.madz.scheduling.meta.ServiceOrderLifecycleMeta;

@Entity
@Table(name = "service_order")
@ReactiveObject
public class ServiceOrder extends ServiceOrderBase implements IServiceOrder, IPlantScheduleOrder, IVehicleScheduleOrder {

    protected static final long serialVersionUID = -6118079224654228286L;

    protected ServiceOrder() {}

    public ServiceOrder(ServiceSummaryPlan parent) {
        parent.addResourceAllocatedTask(this);
        this.summaryPlan = parent;
    }

    @Transition(ServiceOrderLifecycleMeta.Transitions.Start.class)
    public void configureResources(@Relation(ServiceOrderLifecycleMeta.Relations.PlantResource.class) MixingPlantResource plantResource,
            @Relation(ServiceOrderLifecycleMeta.Relations.ConcreteTruckResource.class) ConcreteTruckResource truckResource, double volume) {
        {
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
    public void doStartPlantOrder() {
        this.plantStartedOn = new Date();
    }

    @Override
    public void doFinishPlantOrder() {
        this.plantFinishOn = new Date();
    }

    @Override
    @Transition(PlantScheduleOrderLifecycleMeta.Transitions.Cancel.class)
    public void cancelPlantOrder() {
        this.plantCanceledOn = new Date();
    }

    @Override
    public IServiceOrder getServiceOrder() {
        return this;
    }

    @Override
    public ConcreteTruckResource getConcreteTruckResource() {
        return this.truckResource;
    }

    @Override
    public void doFinishVehicalOrder() {}

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
}
