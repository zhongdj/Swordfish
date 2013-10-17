package net.madz.test.rest;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.eclipsesource.restfuse.Method;
import com.eclipsesource.restfuse.RequestContext;
import com.eclipsesource.restfuse.Response;
import com.eclipsesource.restfuse.annotation.Context;
import com.eclipsesource.restfuse.annotation.HttpTest;
import com.eclipsesource.restfuse.internal.InternalRequest;

public class HttpTestStatement extends com.eclipsesource.restfuse.internal.HttpTestStatement {

    private Description description;
    private String baseUrl;
    private Object target;
    private RequestContext context;

    public HttpTestStatement(Statement base, Description description, Object target, String baseUrl, String proxyHost,
            int proxyPort, RequestContext context) {
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
        try {
            InternalRequest request = buildRequest();
            return callService(request);
        } catch (Throwable t) {
            Logger.getLogger(HttpTestStatement.class.getName()).log(Level.SEVERE, t.getLocalizedMessage(), t);
            throw t;
        }
    }

    private InternalRequest buildRequest() {
        RequestConfiguration requestConfiguration = new RequestConfiguration(baseUrl, description, target);
        return requestConfiguration.createRequest(context);
    }

    public void tryInjectResponse(Response response) {
        for ( Class<? extends Object> eachClass = target.getClass(); !eachClass.equals(Object.class); eachClass = eachClass
                .getSuperclass() ) {
            Field[] fields = eachClass.getDeclaredFields();
            for ( Field field : fields ) {
                Context contextAnnotation = field.getAnnotation(Context.class);
                if ( contextAnnotation != null && field.getType() == Response.class ) {
                    injectResponse(field, response);
                }
            }
        }
        //hack
        VariableContext.getInstance().processResponse(response);
    }

    private void injectResponse(Field field, Response response) {
        field.setAccessible(true);
        try {
            field.set(target, response);
        } catch (Exception exception) {
            throw new IllegalStateException("Could not inject response.", exception);
        }
    }
}
