package net.madz.scheduling.biz;

import net.madz.core.biz.BOProxy;
import net.madz.core.biz.IBizObject;
import net.madz.scheduling.biz.impl.ConcreteTruckResourceBO;
import net.madz.scheduling.entities.ConcreteTruckResource;

@BOProxy(ConcreteTruckResourceBO.class)
public interface IConcreteTruckResource extends IBizObject<ConcreteTruckResource> {

    public static enum StateEnum {
        Assigned,
        Ready,
        PlantDetached,
        OnPassage,
        ConstructionPlantArrived,
        Returning,
        WaitingOrder
    }

    // A. 忙
    // 1 Assigned
    // 2 Ready
    // 3 PlantDetached
    // 4 OnPassage
    // 5 ConstructionPlantArrived
    // 6 Returning
    // B. 闲
    // 7 WaitingOrder
    // 指派任务： 搅拌站， 派车单，包含单位工程信息（地址信息，工程名称，联系人信息），包括浇筑部位信息，混凝土强度等级，外加剂种类，方数
    // 7 -> 1
    // 2 -> 2
    // 3 -> 3
    // 4 -> 4
    // 5 -> 5
    // 6 -> 6
    int assignOrder(IVehicleScheduleOrder order);

    // 空车在场状态时，确认在站候装混凝土
    // 1 -> 2
    void confirmReady();

    // 装完离站
    // 2 -> 3
    void confirmDetachPlant();

    // 出场
    // 3 -> 4
    void confirmLeaveStation();

    // 到工地
    // 4 -> 5
    void confirmArriveToConstructionPlant();

    // 浇筑完毕返场
    // 5 -> 6
    void confirmReturn();

    // 空车进场
    // 6 -> 7
    void confirmArriveStation();

    StateEnum getState();

    /**
     * @return 当前正在执行的派车单
     */
    IVehicleScheduleOrder getWorkingOrder();

    /**
     * @return 当前正在执行以及等待执行的派车单
     */
    IVehicleScheduleOrder[] getQueuedOrder();

    /**
     * @return 额定承载量
     */
    double getRatedCapacity();

    /**
     * @return 车牌号码
     */
    String getLicencePlateNumber();

    /**
     * @return 司机姓名
     */
    String getDriverName();

    /**
     * @return 司机电话号码
     */
    String getDriverPhoneNumber();
}
