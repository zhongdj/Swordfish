package net.madz.test.annotations;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Date;

import net.madz.test.annotations.FreeTrialTenant.ScriptProcessor;
import net.madz.test.stochastic.core.AbsScriptEngine;
import net.madz.test.stochastic.core.TestContext;
import net.madz.test.stochastic.utilities.annotations.Processor;

import org.junit.runners.model.Statement;

import com.eclipsesource.restfuse.Response;
import com.eclipsesource.restfuse.internal.InternalRequest;

@Processor(ScriptProcessor.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface FreeTrialTenant {

    public static final String USER = "test.polaris.metadata";

    public static final String PASS = "1q2w3e4r5t";

    public class EmptyStatement extends Statement {

        @Override
        public void evaluate() throws Throwable {
        }
    }

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
            debug("Creating New Tenant and Inject UserSession");
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
                context.getBase().evaluate();
            } finally {
                username.remove();
                password.remove();
                debug("UserSession had been removed.");
                decreaseIndent();
            }
        }

        private void doRequestFreeTrailTenant(FreeTrialTenant t) throws IOException {
            final String url = "http://localhost:8080/api/auth/register/freeTrial";
            InternalRequest request = new InternalRequest(url);
            request.addHeader("Content-Type", "application/json");
            request.addHeader("Accept", "application/json");
            request.setContentType("application/json");
            InputStream is = getClass().getResourceAsStream("freeTrialTenantTemplate.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            StringBuilder contentBuilder = new StringBuilder();
            while ( null != ( line = br.readLine() ) ) {
                contentBuilder.append(line).append("\n");
            }
            String content = contentBuilder.toString();
            try {
                content = content.replaceAll("#\\{userName\\}", getUsername());
                content = content.replaceAll("#\\{password\\}", getPassword());
                System.out.println(content);
                request.setContent(new ByteArrayInputStream(content.getBytes()));
                Response response = request.post();
                System.out.println(response.getBody());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static String getUsername() {
            return username.get();
        }

        public static String getPassword() {
            return password.get();
        }
    }
}
