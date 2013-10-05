package net.madz.scheduling.biz;

import java.util.List;

import net.madz.common.entities.Additive;
import net.madz.common.entities.Address;
import net.madz.common.entities.Mixture;
import net.madz.core.biz.BOProxy;
import net.madz.core.biz.IBizObject;
import net.madz.scheduling.biz.impl.ServiceSummaryPlanBO;
import net.madz.scheduling.entities.ServiceSummaryPlan;

@BOProxy(ServiceSummaryPlanBO.class)
public interface IServiceSummaryPlan extends IBizObject<ServiceSummaryPlan> {

    /** Non-transitional methods **/
    String getPouringPartName();

    String getUnitProjectName();

    Address getAddress();

    Mixture getMixture();

    List<Additive> getAdditives();

    List<IServiceOrder> getServiceOrderList();

    double getPlannedVolumn();

    boolean getFinished();
}
