package net.madz.rs.contract.resources;

import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.xml.bind.JAXBElement;

import net.madz.contract.sessions.ContractBean;
import net.madz.contract.sessions.CreateContractRequest;
import net.madz.contract.sessions.CreatePouringPartSpecRequest;
import net.madz.core.exceptions.AppServiceException;
import net.madz.utils.BusinessModuleException;

@Path("unitProjects")
public class UnitProjectResources {

    @EJB
    private ContractBean bean;

    @Path("{unitProjectId}")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public void addPouringPartSpec(@PathParam("unitProjectId") Long unitProjectId,
            List<CreatePouringPartSpecRequest> request) throws AppServiceException {
        System.out.println(request.toString());
        try {
            bean.createPouringPartSpecs(unitProjectId, request);
        } catch (BusinessModuleException e) {
            throw new AppServiceException(CreateContractRequest.class, e.getBusinessModuleName(), e.getBundle(),
                    e.getErrorCode());
        }
    }
}
