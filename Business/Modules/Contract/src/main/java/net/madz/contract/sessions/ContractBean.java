package net.madz.contract.sessions;

import javax.annotation.security.RolesAllowed;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import net.madz.authorization.MultitenancyBean;
import net.madz.authorization.interceptor.UserSession.SessionBeanAuthorizationInterceptor;

@Stateless
@LocalBean
@Interceptors(SessionBeanAuthorizationInterceptor.class)
@RolesAllowed({ "admin", "sales" })
public class ContractBean extends MultitenancyBean {

    public CreateContractResponse createContract(CreateContractRequest request) {
        return new CreateContractResponse();
    }
    
    public void createPouringPartSpecs(CreatePouringPartRequest request) {
        
    }
}
