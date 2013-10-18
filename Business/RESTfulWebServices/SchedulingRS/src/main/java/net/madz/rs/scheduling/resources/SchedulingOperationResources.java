package net.madz.rs.scheduling.resources;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import net.madz.contract.spec.entities.PouringPartSpec;
import net.madz.core.exceptions.AppServiceException;
import net.madz.scheduling.entities.ConcreteTruck;
import net.madz.scheduling.entities.ServiceSummaryPlan;
import net.madz.scheduling.sessions.OperationBean;
import net.madz.scheduling.to.CreateConcreteTruckResourceRequest;
import net.madz.scheduling.to.CreateConcreteTruckResourceResponse;
import net.madz.scheduling.to.CreateMixingPlantResourceRequest;
import net.madz.scheduling.to.CreateMixingPlantResourceResponse;
import net.madz.scheduling.to.CreateServiceSummaryPlanRequest;
import net.madz.scheduling.to.CreateServiceSummaryPlanResponse;
import net.madz.scheduling.to.ServiceOrderTO;

@Stateless
@Path("operation")
public class SchedulingOperationResources {

    @EJB
    private OperationBean operation;

    /**
     * @return <big>调度小组</big> 所管辖的正在<big> 建设中 </big>的<big> 浇筑部位技术规格 </big>的集合<br/>
     * <br/>
     *         注： <li>调度部门可以有多个调度小组</li> <li>每个小组可以有多个调度员</li> <li>
     *         在同一个调度小组内的所有调度员共同管辖相同的单位工程集合</li> <li>一个单位工程包含多个浇筑部位</li> <li>
     *         每个浇筑部位可以对应多个浇筑部位技术规格</li> <li>每个浇筑部位技术规格包含一个混凝土（砂浆）强度等级以及多个特殊技术要求
     *         </li> <li>混凝土包含19个强度等级：C10, C15, C20, C25, ..., C100 参见
     *         {@link net.madz.common.entities.Concrete.StrengthGrade}</li> <li>
     *         砂浆包含6个强度等级:M2.5, M5.0, M7.5, M10, M15, M20 参见
     *         {@link net.madz.common.entities.Mortar.StrengthGrade}</li>
     * 
     * 
     */
    @GET
    @Path("pouring-part-specs/by-state/constructing")
    @Produces({ "application/xml", "application/json" })
    public List<PouringPartSpec> listMyPartsInConstructing() {
        return operation.listMyPartsInConstructing();
    }

    /**
     * @param filter
     *            过滤字符串，是一组通过空格分隔的拼音开头字母和数字。<br/>
     *            例如: "XLC16 1CLB" 目标过滤对象为 "新龙城 1层楼板" 所对应的浇筑部位规格
     * 
     * 
     * @return 经过过滤字符串过滤后的<big>调度小组</big> 所管辖的正在<big> 建设中 </big>的<big> 浇筑部位技术规格
     *         </big>的集合<br/>
     * <br/>
     *         注： <li>调度部门可以有多个调度小组</li> <li>每个小组可以有多个调度员</li> <li>
     *         在同一个调度小组内的所有调度员共同管辖相同的单位工程集合</li> <li>一个单位工程包含多个浇筑部位</li> <li>
     *         每个浇筑部位可以对应多个浇筑部位技术规格</li> <li>每个浇筑部位技术规格包含一个混凝土（砂浆）强度等级以及多个特殊技术要求
     *         </li> <li>混凝土包含19个强度等级：C10, C15, C20, C25, ..., C100 参见
     *         {@link net.madz.common.entities.Concrete.StrengthGrade}</li> <li>
     *         砂浆包含6个强度等级:M2.5, M5.0, M7.5, M10, M15, M20 参见
     *         {@link net.madz.common.entities.Mortar.StrengthGrade}</li>
     * 
     * 
     */
    @GET
    @Path("pouring-part-specs/by-state/constructing/{filter}")
    @Produces({ "application/xml", "application/json" })
    public List<PouringPartSpec> filterMyPartsInConstructing(@PathParam("filter") String filter) {
        return operation.filterMyPartsInConstructing(filter);
    }

    /**
     * @return <big>调度小组</big>
     *         针对所管辖<big>单位工程</big>所对应的<big>未完成</big>生产的<big>排产任务</big>的集合<br/>
     * 
     *         注: <big>未完成</big>是<big>排产任务</big>的状态之一,另一个状态是<big>已完成</big>。
     *         任务是否完成的条件取决于是否完成了排产计划预期。<br/>
     *         必要充分条件或为<big>调度员</big> 主观决定 或为 <big>预计生产量已经满足</big> 取决于策略配置
     */
    @GET
    @Path("planned-summary-task/by-state/not-finished")
    @Produces({ "application/xml", "application/json" })
    public List<ServiceSummaryPlan> listNotFinishedPlannedSummaryTasks() {
        return null;
    }

    /**
     * @return 可调度空的砼车列表
     */
    @GET
    @Path("concrete-truck/by-state/empty-available")
    @Produces({ "application/xml", "application/json" })
    public List<ConcreteTruck> listEmptyAvaiableConcreteTrucks() {
        return null;
    }

    /**
     * @param summaryId
     *            排产任务标识(Id)
     * @param resource
     *            为排产任务分配的资源标识对象，包含砼车和搅拌站的标识(Id)
     * @return 已经加入所分配<big>搅拌站</big>生产队列的<big>生产任务</big>
     * @throws AppServiceException
     * 
     */
    @POST
    @Path("summaryPlan/{summaryPlanId}/serviceOrder")
    @Consumes({ "application/xml", "application/json" })
    @Produces({ "application/xml", "application/json" })
    public ServiceOrderTO allocateResource(@PathParam("summaryPlanId") Long summaryPlanId, RequiredResource resource)
            throws AppServiceException {
        return operation.allocateResourceTo(summaryPlanId, resource.mixingPlantResourceId,
                resource.concreteTruckResourceId, resource.volume);
    }

    public static class RequiredResource {

        public Long mixingPlantResourceId;

        public Long concreteTruckResourceId;

        public Double volume;
    }

    @POST
    @Path("mixingPlantResource")
    @Consumes({ "application/xml", "application/json" })
    @Produces({ "application/xml", "application/json" })
    public CreateMixingPlantResourceResponse createPlantResource(CreateMixingPlantResourceRequest request)
            throws AppServiceException {
        return operation.createPlantResource(request);
    }

    @POST
    @Path("concreteTruckResource")
    @Consumes({ "application/xml", "application/json" })
    @Produces({ "application/xml", "application/json" })
    public CreateConcreteTruckResourceResponse createConcreteTruckResource(CreateConcreteTruckResourceRequest cto)
            throws AppServiceException {
        return operation.createConcreteTruckResource(cto);
    }

    @POST
    @Path("serviceSummaryPlan")
    @Consumes({ "application/xml", "application/json" })
    @Produces({ "application/xml", "application/json" })
    public CreateServiceSummaryPlanResponse createServiceSummaryPlan(CreateServiceSummaryPlanRequest sto)
            throws AppServiceException {
        return operation.createServiceSummaryPlan(sto);
    }
}
