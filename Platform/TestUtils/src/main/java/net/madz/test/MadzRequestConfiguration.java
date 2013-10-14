package net.madz.test;

import net.madz.test.annotations.FreeTrialTenant;

import org.junit.runner.Description;

import com.eclipsesource.restfuse.AuthenticationType;
import com.eclipsesource.restfuse.RequestContext;
import com.eclipsesource.restfuse.internal.AuthenticationInfo;
import com.eclipsesource.restfuse.internal.InternalRequest;
import com.eclipsesource.restfuse.internal.RequestConfiguration;

public class MadzRequestConfiguration extends RequestConfiguration {

    public MadzRequestConfiguration(String baseUrl, Description description, Object target) {
        super(baseUrl, description, target);
    }

    @Override
    public InternalRequest createRequest(RequestContext context) {
        
        InternalRequest request = super.createRequest(context);
        final String user = FreeTrialTenant.ScriptProcessor.getUsername();
        final String password = FreeTrialTenant.ScriptProcessor.getPassword();
        if ( null != user ) {
            request.addAuthenticationInfo(new AuthenticationInfo(AuthenticationType.BASIC, user, password));
        }
        return request;
    }
}
