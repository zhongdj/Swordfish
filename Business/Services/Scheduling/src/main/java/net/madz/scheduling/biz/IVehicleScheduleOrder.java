package net.madz.scheduling.biz;

import java.util.Date;

import net.madz.common.entities.Address;
import net.madz.customer.entities.Contact;

public interface IVehicleScheduleOrder {

    public static enum StateEnum {
        Created,
        Loading,
        OnPassage,
        Finished,
        Aborted
    }

    // 指派任务： 搅拌站， 派车单，包含单位工程信息（地址信息，工程名称，联系人信息），包括浇筑部位信息，混凝土强度等级，外加剂种类，方数
    String getConcretePlantName();

    String getUnitProjectName();

    Address getAddress();

    Contact getContact();

    String getPouringPartName();

    String getMixtureStrengthGrade();

    String[] getAdditiveNames();

    double getVolume();

    Date getCreatedOn();

    Date getTransportFinishedOn();

    String getCreatedBy();

    StateEnum getVehicleScheduleOrderState();

    // 1. Created,
    // 2. Loading,
    // 3. On Passage,
    // 4. Finished
    // 5. Aborted
    // 1 -> 2
    void doLoad();

    // 2 -> 3
    void doTransport();

    // 3 -> 4
    void doComplete();

    // 1, 3 -> 5
    void doAbort();
}
