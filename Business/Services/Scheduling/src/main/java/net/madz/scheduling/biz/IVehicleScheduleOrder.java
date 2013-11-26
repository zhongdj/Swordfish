package net.madz.scheduling.biz;

import java.util.Date;

import net.madz.common.entities.Address;
import net.madz.customer.entities.Contact;
import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.StateIndicator;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.annotations.relation.Relation;
import net.madz.scheduling.entities.ConcreteTruckResource;
import net.madz.scheduling.meta.VehicleScheduleOrderLifecycleMeta;

@LifecycleMeta(value = VehicleScheduleOrderLifecycleMeta.class)
public interface IVehicleScheduleOrder {

    long getId();

    // 指派任务： 搅拌站， 派车单，包含单位工程信息（地址信息，工程名称，联系人信息），包括浇筑部位信息，混凝土强度等级，外加剂种类，方数
    String getConcretePlantName();

    String getUnitProjectName();

    Address getAddress();

    Contact getContact();

    String getPouringPartName();

    String getMixtureStrengthGrade();

    String[] getAdditiveNames();

    double getTransportVolume();

    Date getCreatedOn();

    Date getTransportFinishedOn();

    /** Transition methods **/
    @Transition(VehicleScheduleOrderLifecycleMeta.Transitions.Start.class)
    void doLoad();

    @Transition(VehicleScheduleOrderLifecycleMeta.States.Ongoing.CTransitions.DoTransport.class)
    void doTransport();

    @Transition(VehicleScheduleOrderLifecycleMeta.States.Ongoing.CTransitions.DoConstruct.class)
    void doConstruct();

    @Transition(VehicleScheduleOrderLifecycleMeta.States.Ongoing.CTransitions.DoFinish.class)
    void doComplete();

    @Transition(VehicleScheduleOrderLifecycleMeta.Transitions.Finish.class)
    void doFinishVehicalOrder();

    @Transition(VehicleScheduleOrderLifecycleMeta.Transitions.Cancel.class)
    void doAbortOnVehicleScheduleOrder();

    @StateIndicator
    String getVehicleScheduleOrderState();

    @Relation(VehicleScheduleOrderLifecycleMeta.Relations.ServiceOrder.class)
    IServiceOrder getServiceOrder();

    @Relation(VehicleScheduleOrderLifecycleMeta.Relations.ConcreteTruckResource.class)
    ConcreteTruckResource getConcreteTruckResource();
}
