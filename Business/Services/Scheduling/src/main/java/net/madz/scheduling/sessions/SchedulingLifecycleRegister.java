package net.madz.scheduling.sessions;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;

import net.madz.bcel.intercept.DefaultStateMachineRegistry;
import net.madz.scheduling.biz.IPlantScheduleOrder;
import net.madz.scheduling.biz.IServiceOrder;
import net.madz.scheduling.biz.IVehicleScheduleOrder;
import net.madz.scheduling.entities.ConcreteTruckResource;
import net.madz.scheduling.entities.MixingPlantResource;
import net.madz.verification.VerificationException;

@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Singleton
public class SchedulingLifecycleRegister  {

    @PostConstruct
    public void init() {
        final Class<?>[] metaClasses = new Class[] { IPlantScheduleOrder.class, IServiceOrder.class, IVehicleScheduleOrder.class, ConcreteTruckResource.class,
                MixingPlantResource.class };
        for ( Class<?> klass : metaClasses ) {
            try {
                DefaultStateMachineRegistry.getInstance().registerLifecycleMeta(klass);
            } catch (VerificationException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
