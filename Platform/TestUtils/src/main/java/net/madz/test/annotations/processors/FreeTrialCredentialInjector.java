package net.madz.test.annotations.processors;

import net.madz.test.annotations.FreeTrialTenant;

/**
 * Inject FreeTrialTenant's userName and password into JSON template. <br/>
 * <br/>
 * <B>Preconditions:</B><br/>
 * 
 * 1. MUST use with @FreeTrialTenant on test class or on test method. Suggesting
 * to annotate @FreeTrialTenant on test class. ONLY if the test method needs a
 * dependent tenant, then to annotate the @FreeTrialTenant on test method.<br/>
 * 2. Inside the JSON template, use #{userName} and #{password} as placeholder.<br/>
 * 
 * @author Barry
 * 
 */
public class FreeTrialCredentialInjector extends UserCredentialInjector {

    @Override
    public String getUserName() {
        return FreeTrialTenant.ScriptProcessor.getUsername();
    }

    @Override
    public String getPassword() {
        return FreeTrialTenant.ScriptProcessor.getPassword();
    }
}
