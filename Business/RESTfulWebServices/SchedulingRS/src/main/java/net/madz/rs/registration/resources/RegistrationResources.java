package net.madz.rs.registration.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("register")
public class RegistrationResources {

    private static Logger logger = Logger.getLogger(RegistrationResources.class.getName());

    @Path("freeTrial")
    @POST
    @Consumes({ "application/json", "application/xml" })
    public void createFreeTrail(FreeTrailTO freeTrailRequest) {
        logger.info(freeTrailRequest.toString());
    }
}
