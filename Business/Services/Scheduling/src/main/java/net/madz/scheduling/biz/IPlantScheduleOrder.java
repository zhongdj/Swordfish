package net.madz.scheduling.biz;

import java.util.Date;

import net.madz.core.biz.BOProxy;
import net.madz.core.biz.IBizObject;
import net.madz.scheduling.biz.impl.ServiceOrderBO;
import net.madz.scheduling.entities.ServiceOrder;

@BOProxy(ServiceOrderBO.class)
public interface IPlantScheduleOrder extends IBizObject<ServiceOrder> {

    public static enum StateEnum {
        Created,
        Working,
        Done
    }

    public StateEnum getPlantScheduleOrderState();

    void doStartPlantOrder();

    void doFinishPlantOrder();

    String getPlantName();

    String getOperatorName();

    Date getProductionFinishedOn();

    Date getCreatedOn();

    String getCreatedBy();
}
