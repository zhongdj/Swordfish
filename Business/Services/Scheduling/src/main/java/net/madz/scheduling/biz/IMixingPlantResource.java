package net.madz.scheduling.biz;

import net.madz.core.biz.BOProxy;
import net.madz.core.biz.IBizObject;
import net.madz.scheduling.biz.impl.MixingPlantResourceBO;
import net.madz.scheduling.entities.MixingPlantResource;

@BOProxy(MixingPlantResourceBO.class)
public interface IMixingPlantResource extends IBizObject<MixingPlantResource> {

    void assignOrder(IPlantScheduleOrder order);

    IPlantScheduleOrder getWorkingOrder();

    IPlantScheduleOrder[] getQueuedOrders();

    String getName();

    String getOperatorName();

    String getOperatorPhoneNumber();
}
