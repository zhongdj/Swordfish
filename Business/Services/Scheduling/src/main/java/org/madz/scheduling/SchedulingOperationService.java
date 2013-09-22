package org.madz.scheduling;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import net.madz.contract.spec.entities.PouringPartSpec;

@Stateless
@Path("scheduling/operation/")
public class SchedulingOperationService {

	@GET
	@Path("myPouringPartSpecInConstructingList")
	@Produces({ "application/xml", "application/json" })
	public List<PouringPartSpec> listMyPartsInConstructing() {
		final ArrayList<PouringPartSpec> result = new ArrayList<>();
		return result;
	}
}
