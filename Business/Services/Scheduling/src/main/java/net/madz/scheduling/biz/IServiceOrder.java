package net.madz.scheduling.biz;

import net.madz.core.biz.BOProxy;
import net.madz.core.biz.IBizObject;
import net.madz.scheduling.biz.impl.ServiceOrderBO;
import net.madz.scheduling.entities.ServiceOrder;

@BOProxy(ServiceOrderBO.class)
public interface IServiceOrder extends IPlantScheduleOrder, IVehicleScheduleOrder, IBizObject<ServiceOrder> {

    void allocateResources(IMixingPlantResource plantResource, IConcreteTruckResource truckResource, double volume);
}

