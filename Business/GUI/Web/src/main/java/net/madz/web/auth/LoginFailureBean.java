package net.madz.web.auth;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import net.madz.appserv.security.MadzRealm;

/**
 * LoginFailureBean, the model bean of loginError.xhtml is supposed to provide
 * further information about login failures, such as :
 * 
 * 1. User does not exist
 * 
 * 2. Password not correct
 * 
 * 3. User is locked
 * 
 * 4. User is frozen
 * 
 * 5. Tenant is locked
 * 
 * 6. Tenant is frozen
 * 
 * 7. etc.
 * 
 * 
 * So far, we cannot find useful information from the interfaces provided by the
 * Glassfish / Catalina container, such as FacesContext, sessionScope and
 * requestScope.
 * 
 * So we plan to use GlassfishRealm to store ThreadLocal information for
 * handling login failures
 * 
 * @author Barry
 * 
 */
@ManagedBean
@RequestScoped
public class LoginFailureBean {

    public String getUsername() {
        return MadzRealm.getUsername();
    }

    public String getErrorMessage() {
        try {
            if ( null == MadzRealm.getLoginFailure() ) {
                return "Unknown Login Failure.";
            }
            switch (MadzRealm.getLoginFailure()) {
            case PasswordInvalid:
                return "Password Invalid";
            case TenantHasBeenFrozen:
                return "Your company's service account has been frozen";
            case TenantHasBeenLocked:
                return "Your company's service account has been locked";
            case UserHasBeenFrozen:
                return "Your login account has been frozen by administrator, please contact administrator";
            case UserHasBeenLocked:
                return "You login account has been locked due to several login attempt failures, please contact administrator";
            case UsernameNotExist:
                return "Username does not exist.";
            default:
                return "";
            }
        } finally {
            MadzRealm.clearThreadLocals();
        }
    }
}
