package net.madz.test.annotations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URL;
import java.util.Date;

import net.madz.test.annotations.FreeTrialTenant.ScriptProcessor;
import net.madz.test.stochastic.core.AbsScriptEngine;
import net.madz.test.stochastic.core.TestContext;
import net.madz.test.stochastic.utilities.annotations.Processor;
import net.madz.utils.FileUtils;

import com.eclipsesource.restfuse.Response;
import com.eclipsesource.restfuse.internal.InternalRequest;

/**
 * FreeTrialTenant is a test script, which will be interpreted as: <br/>
 * "To create a new free trial tenant in the running test context." <br/>
 * <br/>
 * There are 3 phases: <br/>
 * 1. Before test method execution, create a free trial tenant and register
 * userName and password into thread local variables of
 * FreeTrialTenant.ScriptProcessor.class. <br/>
 * 2. Proceeding with testing statement. <br/>
 * 3. Cleaning up thread local variables. <br/>
 * 
 * @author Barry
 * 
 */
@Processor(ScriptProcessor.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface FreeTrialTenant {

    public static final String USER = "test.polaris.metadata";
    public static final String PASS = "1q2w3e4r5t";

    String username() default USER;

    String password() default PASS;

    String host() default "localhost";

    String port() default "8080";

    String contextRoot() default "api";

    public static class ScriptProcessor extends AbsScriptEngine<FreeTrialTenant> {

        private static final ThreadLocal<String> username = new ThreadLocal<String>();
        private static final ThreadLocal<String> password = new ThreadLocal<String>();

        @Override
        public void doProcess(final TestContext context, FreeTrialTenant t) throws Throwable {
            increaseIndent();
            debug("Creating New Free Trial Tenant ...");
            long time = new Date().getTime();
            if ( USER.equals(t.username()) ) {
                username.set(USER + "." + time + "@gmail.com");
                password.set(PASS);
            } else {
                username.set(t.username());
                password.set(t.password());
            }
            try {
                doRequestFreeTrailTenant(t);
                debug("Free Trial Tenant has been created.");
                context.getBase().evaluate();
            } finally {
                username.remove();
                password.remove();
                debug("Leaving FreeTrialTenant processor");
                decreaseIndent();
            }
        }

        private void doRequestFreeTrailTenant(FreeTrialTenant t) throws IOException {
            final String url = "http://localhost:8080/api/auth/register/freeTrial";
            final InternalRequest request = new InternalRequest(url);
            addHearders(request);
            final URL resource = getClass().getResource("freeTrialTenantTemplate.json");
            String content = FileUtils.readFileContent(resource);
            try {
                content = processContent(content);
                {// format output
                    debug("POST RESTful Request with content:");
                    increaseIndent();
                    debug(content);
                    decreaseIndent();
                }
                request.setContent(new ByteArrayInputStream(content.getBytes()));
                Response response = request.post();
                {
                    debug("Server responsed with: " + response.getStatus());
                    increaseIndent();
                    debug(response.getBody());
                    decreaseIndent();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private String processContent(String content) {
            content = content.replaceAll("#\\{userName\\}", getUsername());
            content = content.replaceAll("#\\{password\\}", getPassword());
            return content;
        }

        private void addHearders(InternalRequest request) {
            request.addHeader("Content-Type", "application/json");
            request.addHeader("Accept", "application/json");
            request.setContentType("application/json");
        }

        public static String getUsername() {
            return username.get();
        }

        public static String getPassword() {
            return password.get();
        }
    }
}
