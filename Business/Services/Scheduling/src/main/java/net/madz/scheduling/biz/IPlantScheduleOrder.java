package net.madz.scheduling.biz;

import java.util.Date;

public interface IPlantScheduleOrder {

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
