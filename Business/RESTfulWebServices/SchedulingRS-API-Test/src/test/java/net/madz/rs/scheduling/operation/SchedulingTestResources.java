package net.madz.rs.scheduling.operation;

import java.util.logging.Logger;

import net.madz.test.rest.Destination;
import net.madz.test.rest.annotations.VariableInjector;
import net.madz.test.rest.annotations.processors.FreeTrialCredentialInjector;

import org.junit.Rule;

import com.eclipsesource.restfuse.Method;
import com.eclipsesource.restfuse.Response;
import com.eclipsesource.restfuse.annotation.Context;
import com.eclipsesource.restfuse.annotation.Header;
import com.eclipsesource.restfuse.annotation.HttpTest;

public class SchedulingTestResources {

    protected static Logger logger = Logger.getLogger(SchedulingTestResources.class.getName());
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json;charset=UTF8";
    public static final String ACCEPT = "Accept";
    @Rule
    public Destination destination = new Destination(this, "http://localhost:8080/api");
    @Context
    protected Response response;

    public static interface Post {

        public static class ConcreteTruckResources extends SchedulingTestResources {

            public static final String URI = "/scheduling/operation/concreteTruckResource";

            public static class UriParams {}
            public static class MergeFields {}
            public static class Extractors {

                public static final String ID = "id";
            }

            @HttpTest(method = Method.POST, path = URI, file = "base.concrete.truck.resource.json", headers = {
                    @Header(name = CONTENT_TYPE, value = APPLICATION_JSON),
                    @Header(name = ACCEPT, value = APPLICATION_JSON) })
            public void create() {
                logger.info(response.getBody());
            }
        }
        public static class Contracts extends SchedulingTestResources {

            public static final String URI = "/contracting/contracts";

            public static class UriParams {}
            public static class MergeFields {}
            public static class Extractors {

                public static final String UNIT_PROJECT_ID = "unitProjectIds[0].id";
            }

            @HttpTest(method = Method.POST, path = URI, file = "base.contract.json", headers = {
                    @Header(name = CONTENT_TYPE, value = APPLICATION_JSON),
                    @Header(name = ACCEPT, value = APPLICATION_JSON) })
            public void create() {
                logger.info(response.getBody());
            }
        }
        public static class PlantResources extends SchedulingTestResources {

            public static final String URI = "/scheduling/operation/mixingPlantResource";

            public static class UriParams {}
            public static class MergeFields {

                public static final String USERNAME = "userName";
            }
            public static class Extractors {

                public static final String ID = "id";
            }

            @HttpTest(method = Method.POST, path = URI, file = "base.mixing.plant.resource.json", headers = {
                    @Header(name = CONTENT_TYPE, value = APPLICATION_JSON),
                    @Header(name = ACCEPT, value = APPLICATION_JSON) })
            @VariableInjector(FreeTrialCredentialInjector.class)
            public void create() {
                logger.info(response.getBody());
            }
        }
        public static class PartSpecs extends SchedulingTestResources {

            public static final String URI = "contracting/unitProjects/{unitProjectId}";

            public static class UriParams {

                public static final String UNIT_PROJECT_ID = "unitProjectId";
            }
            public static class MergeFields {

                public static final String USERNAME = "userName";
            }
            public static class Extractors {

                public static final String PART_SPEC_ID = "pouringPartSpecIds[0].id";
            }

            @HttpTest(method = Method.POST, path = URI, file = "base.part.spec.json", headers = {
                    @Header(name = CONTENT_TYPE, value = APPLICATION_JSON),
                    @Header(name = ACCEPT, value = APPLICATION_JSON) })
           public  void create() {
                logger.info(response.getBody());
            }
        }
        public static class SummaryPlans extends SchedulingTestResources {

            public static final String URI = "/scheduling/operation/serviceSummaryPlan";

            public static class UriParams {}
            public static class MergeFields {

                public static final String SPEC_ID = "specId";
            }
            public static class Extractors {

                public static final String ID = "id";
            }

            @HttpTest(method = Method.POST, path = URI, file = "base.summary.plan.json", headers = {
                    @Header(name = CONTENT_TYPE, value = APPLICATION_JSON),
                    @Header(name = ACCEPT, value = APPLICATION_JSON) })
            public void create() {
                logger.info(response.getBody());
            }
        }
    }
}
