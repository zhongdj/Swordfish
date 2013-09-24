package net.madz.rs.scheduling.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import net.madz.authorization.entities.Tenant;
import net.madz.contract.spec.entities.PouringPartSpec;
import net.madz.scheduling.OperationBean;
import net.madz.scheduling.entities.PlannedSummaryTask;
import net.madz.scheduling.entities.ResourceAllocatedTask;

@Stateless
@Path("operation")
public class SchedulingOperationResources {

    @EJB
    private OperationBean operation;

    /**
     * 返回 <big>调度小组</big> 所管辖的正在<big> 建设中 </big>的<big> 浇筑部位技术规格 </big>的集合<br/>
     * <br/>
     * 注： <li>调度部门可以有多个调度小组</li> <li>每个小组可以有多个调度员</li> <li>
     * 在同一个调度小组内的所有调度员共同管辖相同的单位工程集合</li> <li>一个单位工程包含多个浇筑部位</li> <li>
     * 每个浇筑部位可以对应多个浇筑部位技术规格</li> <li>每个浇筑部位技术规格包含一个混凝土（砂浆）强度等级以及多个特殊技术要求</li>
     * <li>混凝土包含19个强度等级：C10, C15, C20, C25, ..., C100 参见
     * {@link net.madz.common.entities.Concrete.StrengthGrade}</li> <li>
     * 砂浆包含6个强度等级:M2.5, M5.0, M7.5, M10, M15, M20 参见
     * {@link net.madz.common.entities.Mortar.StrengthGrade}</li>
     * 
     * 
     */
    @GET
    @Path("pouring-part-specs/by-state/constructing")
    @Produces({ "application/xml", "application/json" })
    public List<PouringPartSpec> listMyPartsInConstructing() {
        ArrayList<Tenant> result = new ArrayList<Tenant>();
        return operation.listMyPartsInConstructing();
    }

    @GET
    @Path("planned-summary-task/by-state/not-finished")
    @Produces({ "application/xml", "application/json" })
    public List<PlannedSummaryTask> listNotFinishedPlannedSummaryTasks() {
        return null;
    }

    @POST
    @Path("planned-summary-task/{summaryId}/resource-allocated-task")
    @Consumes({ "application/xml", "application/json" })
    @Produces({ "application/xml", "application/json" })
    public ResourceAllocatedTask allocateResource(@PathParam("{summaryId}") Long summaryId, RequiredResource resource) {
        return operation.allocateResourceTo(summaryId, resource.mixingPlantId, resource.concreteTruckId);
    }

    public static class RequiredResource {

        public Long mixingPlantId;

        public Long concreteTruckId;
    }
}
