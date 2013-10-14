package net.madz.rs.contract.resources;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import net.madz.contract.sessions.ContractBean;
import net.madz.contract.sessions.CreateContractRequest;
import net.madz.contract.sessions.CreateContractResponse;
import net.madz.core.exceptions.AppServiceException;
import net.madz.utils.BusinessModuleException;

@Path("contracts")
public class ContractResources {

    @EJB
    ContractBean bean;

    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public CreateContractResponse createContract(CreateContractRequest request) throws AppServiceException {
        System.out.println(request.toString());
        try {
            return bean.createContract(request);
        } catch (BusinessModuleException e) {
            throw new AppServiceException(CreateContractRequest.class, e.getBusinessModuleName(), e.getBundle(),
                    e.getErrorCode());
        }
    }
}
