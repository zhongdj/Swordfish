package net.madz.scheduling.biz;

import java.util.List;

import net.madz.common.entities.Additive;
import net.madz.common.entities.Address;
import net.madz.common.entities.Mixture;
import net.madz.core.biz.BOProxy;
import net.madz.core.biz.IBizObject;
import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.StateIndicator;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.annotations.action.Condition;
import net.madz.scheduling.biz.impl.ServiceSummaryPlanBO;
import net.madz.scheduling.entities.ServiceSummaryPlan;
import net.madz.scheduling.meta.SummaryPlanLifecycleMeta;
import net.madz.scheduling.meta.SummaryPlanLifecycleMeta.Conditions.VolumeMeasurable;

@BOProxy(ServiceSummaryPlanBO.class)
@LifecycleMeta(value = SummaryPlanLifecycleMeta.class)
public interface IServiceSummaryPlan extends IBizObject<ServiceSummaryPlan>, VolumeMeasurable {

    /** Non-transitional methods **/
    String getPouringPartName();

    String getUnitProjectName();

    Address getAddress();

    Mixture getMixture();

    List<Additive> getAdditives();

    List<IServiceOrder> getServiceOrderList();

    double getPlannedVolume();

    /** Transitional methods **/
    @Transition(SummaryPlanLifecycleMeta.Transitions.createServiceOrder.class)
    IServiceOrder createServiceOrder(IMixingPlantResource plantResource, IConcreteTruckResource truckResource, double volume);

    @Transition(SummaryPlanLifecycleMeta.Transitions.AdjustTotalVolume.class)
    void adjustTotalVolume(double newTotalVolume);

    @Transition(SummaryPlanLifecycleMeta.Transitions.confirmFinish.class)
    void confirmFinish();

    @Condition(SummaryPlanLifecycleMeta.Conditions.VolumeMeasurable.class)
    VolumeMeasurable getVolumeMeasurable();

    @StateIndicator
    String getState();
}
