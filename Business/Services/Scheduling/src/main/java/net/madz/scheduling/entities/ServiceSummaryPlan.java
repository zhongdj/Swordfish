package net.madz.scheduling.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import net.madz.lifecycle.annotations.LifecycleMeta;
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
public class ServiceSummaryPlan extends ServiceSummaryPlanBase implements VolumeMeasurable {

    protected static final long serialVersionUID = -2519583821494066599L;

    @Transition(CreateServiceOrder.class)
    public IServiceOrder createServiceOrder(MixingPlantResource plantResource, ConcreteTruckResource truckResource, double volume) {
        IServiceOrder order = createServiceOrder();
        order.configureResources(plantResource, truckResource, volume);
        return order;
    }

    protected IServiceOrder createServiceOrder() {
        final ServiceOrder serviceOrder = new ServiceOrder(this);
        serviceOrder.init();
        return serviceOrder;
    }

    @Transition(AdjustTotalVolume.class)
    public void adjustTotalVolume(double newTotalVolume) {
        this.totalVolume = newTotalVolume;
    }

    @Transition(ServiceSummaryPlanLifecycleMeta.Transitions.ConfirmFinish.class)
    public void confirmFinish() {
        this.finishedOn = new Date();
    }

    @Transition(ServiceSummaryPlanLifecycleMeta.Transitions.Cancel.class)
    public void cancel() {
        this.canceledOn = new Date();
    }

    @Condition(VolumeMeasurable.class)
    public VolumeMeasurable getVolumeMeasurable() {
        return this;
    }

    public boolean isVolumeLeft() {
        final double leftValue = this.getTotalVolume() - this.getPlannedVolume();
        return leftValue > 0;
    }
}
