package net.madz.rs.scheduling.operation;

import javax.xml.bind.JAXBException;

import net.madz.rs.scheduling.operation.SchedulingTestResources.Post.ConcreteTruckResources;
import net.madz.rs.scheduling.operation.SchedulingTestResources.Post.Contracts;
import net.madz.rs.scheduling.operation.SchedulingTestResources.Post.PartSpecs;
import net.madz.rs.scheduling.operation.SchedulingTestResources.Post.PlantResources;
import net.madz.rs.scheduling.operation.SchedulingTestResources.Post.SummaryPlans;
import net.madz.rs.scheduling.providers.ErrorTO;
import net.madz.scheduling.to.ServiceOrderTO;
import net.madz.test.annotations.FreeTrialTenant;
import net.madz.test.rest.Destination;
import net.madz.test.rest.HttpUnitRunner;
import net.madz.test.rest.annotations.Create;
import net.madz.test.rest.annotations.Creates;
import net.madz.test.rest.annotations.Extractor;
import net.madz.test.rest.annotations.MergeField;
import net.madz.test.rest.annotations.MergeFields;
import net.madz.test.rest.annotations.UriInjector;
import net.madz.test.rest.annotations.UriParam;
import net.madz.utils.MOXyUtils;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.runner.RunWith;

import com.eclipsesource.restfuse.Method;
import com.eclipsesource.restfuse.Response;
import com.eclipsesource.restfuse.annotation.Context;
import com.eclipsesource.restfuse.annotation.Header;
import com.eclipsesource.restfuse.annotation.HttpTest;

@RunWith(HttpUnitRunner.class)
@FreeTrialTenant
@Creates({
        @Create(action = Contracts.class, extractors = { @Extractor(key = Contracts.Extractors.UNIT_PROJECT_ID,
                var = "unitProjectId") }),
        @Create(action = PartSpecs.class, uriParams = { @UriParam(key = PartSpecs.UriParams.UNIT_PROJECT_ID,
                var = "unitProjectId") }, extractors = { @Extractor(key = PartSpecs.Extractors.PART_SPEC_ID,
                var = "pouringPartSpecId") }),
        @Create(action = SummaryPlans.class, mergeFields = { @MergeField(key = SummaryPlans.MergeFields.SPEC_ID,
                var = "pouringPartSpecId") }, extractors = { @Extractor(key = SummaryPlans.Extractors.ID,
                var = "summaryPlanId") }),
        @Create(action = PlantResources.class, extractors = { @Extractor(key = PlantResources.Extractors.ID,
                var = "mixingPlantResourceId") }),
        @Create(action = ConcreteTruckResources.class, extractors = { @Extractor(
                key = ConcreteTruckResources.Extractors.ID, var = "concreteTruckResourceId") }) })
public class AllocateResourceTest {

    private static final String ACCEPT = "Accept";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String CREATE_SERVICE_ORDER_URI = "/scheduling/operation/summaryPlan/{summaryPlanId}/serviceOrder";
    @Rule
    public Destination destination = new Destination(this, "http://localhost:8080/api");
    @Context
    private Response response;

    @HttpTest(method = Method.POST, path = CREATE_SERVICE_ORDER_URI, file = "positive.allocate.resource.json",
            headers = { @Header(name = CONTENT_TYPE, value = APPLICATION_JSON),
                    @Header(name = ACCEPT, value = APPLICATION_JSON) })
    @UriInjector(param = "summaryPlanId", var = "summaryPlanId")
    @MergeFields({ @MergeField(key = "plantResourceId", var = "mixingPlantResourceId"),
            @MergeField(key = "truckResourceId", var = "concreteTruckResourceId") })
    public void testMethod() throws JAXBException {
        System.out.println(response.getBody());
        com.eclipsesource.restfuse.Assert.assertOk(response);
        ServiceOrderTO value = MOXyUtils
                .unmarshal(response, ServiceOrderTO.class, new Class[] { ServiceOrderTO.class });
        System.out.println(value);
    }

    @HttpTest(method = Method.POST, path = CREATE_SERVICE_ORDER_URI, file = "positive.allocate.resource.json",
            headers = { @Header(name = CONTENT_TYPE, value = APPLICATION_JSON),
                    @Header(name = ACCEPT, value = APPLICATION_JSON) })
    @UriInjector(param = "summaryPlanId", var = "summaryPlanId")
    public void test_invalid_summaryPlanId() throws JAXBException {
        System.out.println(response.getBody());
        com.eclipsesource.restfuse.Assert.assertNotFound(response);
        ErrorTO value = MOXyUtils.unmarshal(response, ErrorTO.class, new Class[] { ErrorTO.class });
        Assert.assertEquals("100-0001", value.getErrorCode());
        System.out.println(value);
    }

    @HttpTest(method = Method.POST, path = CREATE_SERVICE_ORDER_URI,
            file = "allocate.resource.invalid.plantResourceId.json", headers = {
                    @Header(name = CONTENT_TYPE, value = APPLICATION_JSON),
                    @Header(name = ACCEPT, value = APPLICATION_JSON) })
    @UriInjector(param = "summaryPlanId", var = "summaryPlanId")
    public void test_invalid_plantResourceId() throws JAXBException {
        System.out.println(response.getBody());
        com.eclipsesource.restfuse.Assert.assertNotFound(response);
        ErrorTO value = MOXyUtils.unmarshal(response, ErrorTO.class, new Class[] { ErrorTO.class });
        Assert.assertEquals("100-0006", value.getErrorCode());
        System.out.println(value);
    }

    @HttpTest(method = Method.POST, path = CREATE_SERVICE_ORDER_URI,
            file = "allocate.resource.invalid.concreteTruckResourceId.json", headers = {
                    @Header(name = CONTENT_TYPE, value = APPLICATION_JSON),
                    @Header(name = ACCEPT, value = APPLICATION_JSON) })
    @UriInjector(param = "summaryPlanId", var = "summaryPlanId")
    public void test_invalid_concreteTruckResourceId() throws JAXBException {
        System.out.println(response.getBody());
        com.eclipsesource.restfuse.Assert.assertNotFound(response);
        ErrorTO value = MOXyUtils.unmarshal(response, ErrorTO.class, new Class[] { ErrorTO.class });
        Assert.assertEquals("100-0004", value.getErrorCode());
        System.out.println(value);
    }
}
