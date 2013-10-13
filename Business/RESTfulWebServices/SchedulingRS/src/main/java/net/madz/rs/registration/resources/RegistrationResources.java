package net.madz.rs.registration.resources;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import net.madz.authorization.sessions.AuthorizationBean;
import net.madz.authorization.sessions.FreeTrailTO;
import net.madz.core.exceptions.AppServiceException;
import net.madz.utils.BusinessModuleException;

@Stateless
@Path("register")
public class RegistrationResources {

    private static Logger logger = Logger.getLogger(RegistrationResources.class.getName());

    @EJB
    private AuthorizationBean authBean;

    @Path("freeTrial")
    @POST
    @Consumes({ "application/json", "application/xml" })
    public void createFreeTrail(FreeTrailTO freeTrailRequest) throws AppServiceException {
        logger.info(freeTrailRequest.toString());
        try {
            authBean.registerFreeTrial(freeTrailRequest);
        } catch (BusinessModuleException e) {
            logger.log(Level.SEVERE, e.getErrorCode() + ": " + e.getMessage(), e);
            throw new AppServiceException(AuthorizationBean.class, e.getBusinessModuleName(), e.getBundle(),
                    e.getErrorCode());
        }
    }
}
