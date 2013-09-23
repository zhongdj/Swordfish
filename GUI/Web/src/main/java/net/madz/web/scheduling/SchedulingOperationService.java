package net.madz.web.scheduling;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import net.madz.authorization.entities.Tenant;
import net.madz.contract.spec.entities.PouringPartSpec;

import org.madz.scheduling.OperationBean;

@Stateless
@Path("operation")
public class SchedulingOperationService {

    @EJB
    private OperationBean operation;

    @GET
    @Path("myPouringPartSpecInConstructingList")
    @Produces({ "application/xml", "application/json" })
    public List<PouringPartSpec> listMyPartsInConstructing() {
        ArrayList<Tenant> result = new ArrayList<Tenant>();
        
        return operation.listMyPartsInConstructing();
    }
}
