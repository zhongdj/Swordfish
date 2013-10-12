package net.madz.scheduling.biz;

import net.madz.core.biz.BOProxy;
import net.madz.core.biz.IBizObject;
import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.StateIndicator;
import net.madz.lifecycle.annotations.Transition;
import net.madz.scheduling.biz.impl.ConcreteTruckResourceBO;
import net.madz.scheduling.entities.ConcreteTruckResource;
import net.madz.scheduling.meta.ConcreteTruckResourceLifecycleMeta;

@BOProxy(ConcreteTruckResourceBO.class)
@LifecycleMeta(value = ConcreteTruckResourceLifecycleMeta.class)
public interface IConcreteTruckResource extends IBizObject<ConcreteTruckResource> {

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

    /** Transition methods **/
    @Transition(ConcreteTruckResourceLifecycleMeta.Transitions.Assign.class)
    int assignOrder(IVehicleScheduleOrder order);

    @Transition(ConcreteTruckResourceLifecycleMeta.Transitions.Release.class)
    void finishOrder(Long orderId);

    @Transition(ConcreteTruckResourceLifecycleMeta.Transitions.Detach.class)
    void detach();

    @StateIndicator
    String getState();
}
