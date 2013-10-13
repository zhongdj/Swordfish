package net.madz.rs.contract.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("contracts")
public class ContractResources {

    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public CreateContractResponseTO createContract(CreateContractRequestTO request) {
        System.out.println(request.toString());
        return new CreateContractResponseTO();
    }
}
