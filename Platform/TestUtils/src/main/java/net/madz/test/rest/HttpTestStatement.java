package net.madz.test.rest;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.eclipsesource.restfuse.Method;
import com.eclipsesource.restfuse.RequestContext;
import com.eclipsesource.restfuse.Response;
import com.eclipsesource.restfuse.annotation.HttpTest;
import com.eclipsesource.restfuse.internal.InternalRequest;
import com.eclipsesource.restfuse.internal.RequestConfiguration;

public class HttpTestStatement extends com.eclipsesource.restfuse.internal.HttpTestStatement {

    private Description description;

    private String baseUrl;

    private Object target;

    private RequestContext context;

    public HttpTestStatement(Statement base, Description description, Object target, String baseUrl,
            String proxyHost, int proxyPort, RequestContext context) {
        super(base, description, target, baseUrl, proxyHost, proxyPort, context);
        this.description = description;
        this.baseUrl = baseUrl;
        this.target = target;
        this.context = context;
    }

    private Response callService(InternalRequest request) {
        Method requestMethod = description.getAnnotation(HttpTest.class).method();
        Response result = null;
        if ( requestMethod.equals(Method.GET) ) {
            result = request.get();
        } else if ( requestMethod.equals(Method.POST) ) {
            result = request.post();
        } else if ( requestMethod.equals(Method.DELETE) ) {
            result = request.delete();
        } else if ( requestMethod.equals(Method.PUT) ) {
            result = request.put();
        } else if ( requestMethod.equals(Method.HEAD) ) {
            result = request.head();
        } else if ( requestMethod.equals(Method.OPTIONS) ) {
            result = request.options();
        }
        return result;
    }

    public Response sendRequest() {
        InternalRequest request = buildRequest();
        return callService(request);
    }

    private InternalRequest buildRequest() {
        RequestConfiguration requestConfiguration = new RequestConfiguration(baseUrl, description, target);
        return requestConfiguration.createRequest(context);
    }
}
