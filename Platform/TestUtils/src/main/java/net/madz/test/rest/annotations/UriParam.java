package net.madz.test.rest.annotations;

/**
 * The @UriParam describes merging metadata mapping from context
 * variable to RESTful POST request URI parameter. <br/>
 * <br/>
 * For example, <i><b>@UriParam(key="serviceSummaryPlanId",
 * var="summaryPlanId")</b></i><br/>
 * describes mapping information from context variable summaryPlanId to
 * UriParam {serviceSummaryPlanId} in following RESTful POST request URI:<br/>
 * /scheduling/operation/summaryPlan/<i><b>{serviceSummaryPlanId}</b></i>
 * serviceOrder.<br/>
 * 
 * 
 * 
 * @author Tracy Lu
 * 
 */
public @interface UriParam {

    /**
     * @return RESTful POST request Uri parameter name to be merged, such as a request Uri
     *         parameter {serviceSummaryPlanId} of Uri <br/>
     *         /scheduling/operation/summaryPlan/<i><b>{serviceSummaryPlanId}</
     *         b></i>
     *         can be represented by <i><b>key="serviceSummaryPlanId"</b></i>.
     */
    String key();

    /**
     * @return Context variable name, such as a context variable
     *         <i><b>${summaryPlanId}</b></i> can be represented by
     *         <i><b>var="summaryPlanId"</b></i>.
     */
    String var();
}
