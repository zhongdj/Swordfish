package net.madz.rs.scheduling.operation;

import com.eclipsesource.restfuse.Method;
import com.eclipsesource.restfuse.annotation.Header;
import com.eclipsesource.restfuse.annotation.HttpTest;

public class ResourceActions {

    static class CreateConcreteTruckAction implements HttpTestAction {

        @HttpTest(method = Method.POST, path = "http://localhost:8080/api/scheduling/operation/concreteTruckResource",
                file = "base.concrete.truck.resource.json", headers = {
                        @Header(name = CONTENT_TYPE, value = APPLICATION_JSON),
                        @Header(name = ACCEPT, value = APPLICATION_JSON) })
        public void doAction() {
        }
    }
}
