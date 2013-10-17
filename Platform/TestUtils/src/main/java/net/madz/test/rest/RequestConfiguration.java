package net.madz.test.rest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.madz.test.annotations.FreeTrialTenant;
import net.madz.test.rest.annotations.VariableInjector;
import net.madz.test.rest.annotations.processors.TemplateProcessor;
import net.madz.utils.FileUtils;

import org.junit.runner.Description;

import com.eclipsesource.restfuse.AuthenticationType;
import com.eclipsesource.restfuse.MediaType;
import com.eclipsesource.restfuse.RequestContext;
import com.eclipsesource.restfuse.annotation.Authentication;
import com.eclipsesource.restfuse.annotation.Header;
import com.eclipsesource.restfuse.annotation.HttpTest;
import com.eclipsesource.restfuse.internal.AuthenticationInfo;
import com.eclipsesource.restfuse.internal.InternalRequest;

public class RequestConfiguration extends com.eclipsesource.restfuse.internal.RequestConfiguration {

    private static final String PATH_SEPARATOR = "/";

    private final String baseUrl;

    private final Description description;

    private final Object target;

    public RequestConfiguration(String baseUrl, Description description, Object target) {
        super(baseUrl, description, target);
        this.baseUrl = baseUrl;
        this.description = description;
        this.target = target;
    }

    @Override
    public InternalRequest createRequest(RequestContext context) {
        HttpTest call = description.getAnnotation(HttpTest.class);
        String rawPath = combineUrlAndPath(baseUrl, call.path());
        InternalRequest request = new InternalRequest(substituePathSegments(rawPath, context));
        addAuthentication(call, request);
        addContentType(call, request);
        addHeader(call, request, context);
        addBody(call, request);
        return request;
    }

    // ////////////////////////////////////////////////////////////////////////
    // hacking part Start
    // ////////////////////////////////////////////////////////////////////////
    private String transformContent(String content) {
        Method method = null;
        VariableInjector injector = null;
        try {
            method = this.description.getTestClass().getMethod(this.description.getMethodName());
            injector = method.getAnnotation(VariableInjector.class);
            if ( null == injector ) {
                return content;
            }
            TemplateProcessor processor;
            try {
                processor = injector.value().newInstance();
                return processor.process(content);
            } catch (Exception e) {
                throw new IllegalStateException("Cannot instantiate injector instance: " + injector.value(), e);
            }
        } catch (NoSuchMethodException e) {
            return content;
        } catch (SecurityException e) {
            return content;
        }
    }

    /**
     * @param file
     * @return
     */
    private InputStream transformFileStream(String file) {
        URL resource = target.getClass().getResource(file);
        if ( null == this.description.getMethodName() ) {
            return openFile(file, resource);
        }
        Method method = null;
        VariableInjector injector = null;
        try {
            method = this.description.getTestClass().getMethod(this.description.getMethodName());
            injector = method.getAnnotation(VariableInjector.class);
            if ( null == injector ) {
                return openFile(file, resource);
            }
        } catch (NoSuchMethodException e) {
            return openFile(file, resource);
        } catch (SecurityException e) {
            return openFile(file, resource);
        }
        String content = FileUtils.readFileContent(resource);
        TemplateProcessor injectProcessor;
        try {
            injectProcessor = injector.value().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot instantiate injector instance: " + injector.value(), e);
        }
        return getContentStream(injectProcessor.process(content));
    }

    // ////////////////////////////////////////////////////////////////////////
    // hacking part End
    // ////////////////////////////////////////////////////////////////////////
    private void addAuthentication(HttpTest call, InternalRequest request) {
        final String user = FreeTrialTenant.ScriptProcessor.getUsername();
        final String password = FreeTrialTenant.ScriptProcessor.getPassword();
        if ( null != user ) {
            request.addAuthenticationInfo(new AuthenticationInfo(AuthenticationType.BASIC, user, password));
        } else {
            Authentication[] authentications = call.authentications();
            if ( authentications != null ) {
                for ( Authentication authentication : authentications ) {
                    AuthenticationType type = authentication.type();
                    String iUser = authentication.user();
                    String iPassword = authentication.password();
                    request.addAuthenticationInfo(new AuthenticationInfo(type, iUser, iPassword));
                }
            }
        }
    }

    private void addContentType(HttpTest call, InternalRequest request) {
        MediaType contentType = call.type();
        if ( contentType != null ) {
            request.setContentType(contentType.getMimeType());
        }
    }

    private void addHeader(HttpTest call, InternalRequest request, RequestContext context) {
        addHeadersFromContext(request, context);
        addHeadersFromAnnotation(call, request);
    }

    private void addHeadersFromContext(InternalRequest request, RequestContext context) {
        if ( context != null && !context.getHeaders().isEmpty() ) {
            Map<String, String> headers = context.getHeaders();
            for ( String name : headers.keySet() )
                request.addHeader(name, headers.get(name));
        }
    }

    private void addHeadersFromAnnotation(HttpTest call, InternalRequest request) {
        Header[] header = call.headers();
        if ( header != null ) {
            for ( Header parameter : header ) {
                request.addHeader(parameter.name(), parameter.value());
            }
        }
    }

    private void addBody(HttpTest test, InternalRequest request) {
        if ( !test.file().equals("") ) {
            request.setContent(transformFileStream(test.file()));
        } else if ( !test.content().equals("") ) {
            request.setContent(getContentStream(transformContent(test.content())));
        }
    }

    private InputStream openFile(String file, URL resource) {
        try {
            return resource.openStream();
        } catch (Exception ioe) {
            throw new IllegalStateException("Could not open file " + file + ". Maybe it's not on the classpath?");
        }
    }

    private InputStream getContentStream(String content) {
        try {
            return new ByteArrayInputStream(content.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException shouldNotHappen) {
            throw new IllegalStateException(shouldNotHappen);
        }
    }

    private String substituePathSegments(String path, RequestContext context) {
        String substitutedPath = path;
        Pattern pattern = Pattern.compile(".*?\\{(.*?)\\}.*?");
        Matcher matcher = pattern.matcher(path);
        while ( matcher.find() ) {
            String segment = matcher.group(1);
            checkSubstitutionExists(context, segment);
            substitutedPath = substitutedPath.replace("{" + segment + "}", context.getPathSegments().get(segment));
        }
        return substitutedPath;
    }

    private void checkSubstitutionExists(RequestContext context, String segment) {
        if ( !context.getPathSegments().containsKey(segment) ) {
            throw new IllegalStateException("Misconfigured Destination. Could not replace {" + segment + "}.");
        }
    }

    private String combineUrlAndPath(String url, String pathValue) {
        String result;
        if ( url.endsWith(PATH_SEPARATOR) && pathValue.startsWith(PATH_SEPARATOR) ) {
            result = url + pathValue.substring(1, pathValue.length());
        } else if ( ( !url.endsWith(PATH_SEPARATOR) && pathValue.startsWith(PATH_SEPARATOR) )
                || ( url.endsWith(PATH_SEPARATOR) && !pathValue.startsWith(PATH_SEPARATOR) ) ) {
            result = url + pathValue;
        } else if ( !url.endsWith(PATH_SEPARATOR) && !pathValue.startsWith(PATH_SEPARATOR) ) {
            result = url + PATH_SEPARATOR + pathValue;
        } else {
            throw new IllegalStateException("Invalid url format with base url " + url + " and path " + pathValue);
        }
        return result;
    }
}
