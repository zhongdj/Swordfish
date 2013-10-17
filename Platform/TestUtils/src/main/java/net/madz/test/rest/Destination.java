package net.madz.test.rest;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.eclipsesource.restfuse.RequestContext;
import com.eclipsesource.restfuse.annotation.HttpTest;
import com.eclipsesource.restfuse.internal.HttpTestStatement;

public class Destination implements TestRule {

    public Destination(Object testObject, String baseUrl, String proxyHost, int proxyPort) {
        this(testObject, baseUrl);
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.context = new RequestContext();
    }

    public Destination(Object testObject, String baseUrl) {
        checkBaseUrl(baseUrl);
        checkTestObject(testObject);
        this.testObject = testObject;
        this.baseUrl = baseUrl;
        this.context = new RequestContext();
    }

    private HttpTestStatement requestStatement;

    private final String baseUrl;

    private String proxyHost;

    private int proxyPort;

    private RequestContext context;

    private Object testObject;

    /**
     * Access to context to define additional request properties at runtime
     * 
     * @return context to be manipulated
     */
    public RequestContext getRequestContext() {
        return context;
    }

    private void checkBaseUrl(String baseUrl) {
        if ( baseUrl == null ) {
            throw new IllegalArgumentException("baseUrl must not be null");
        }
        try {
            new URL(baseUrl);
        } catch (MalformedURLException mue) {
            throw new IllegalArgumentException("baseUrl has to be an URL");
        }
    }

    private void checkTestObject(Object testObject) {
        if ( testObject == null ) {
            throw new IllegalArgumentException("testObject must not be null.");
        }
    }

    @Override
    /**
     * <p><b>Not meant for public use. This method will be invoked by the JUnit framework.</b></p>
     */
    public Statement apply(Statement base, Description description) {
        Statement result;
        if ( hasAnnotation(description) ) {
            requestStatement = new HttpTestStatement(base, description, testObject, baseUrl, proxyHost, proxyPort,
                    context);
            result = requestStatement;
        } else {
            result = base;
        }
        return result;
    }

    private boolean hasAnnotation(Description description) {
        return description.getAnnotation(HttpTest.class) != null;
    }
}
