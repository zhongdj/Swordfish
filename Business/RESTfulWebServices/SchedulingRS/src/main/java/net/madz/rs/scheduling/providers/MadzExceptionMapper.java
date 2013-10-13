package net.madz.rs.scheduling.providers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import net.madz.core.exceptions.AppServiceException;
import net.madz.scheduling.BONotFoundException;
import net.madz.utils.MadzException;

@Provider
public class MadzExceptionMapper implements ExceptionMapper<MadzException> {

    @Override
    public Response toResponse(MadzException e) {
        final Response result;
        if ( e instanceof BONotFoundException ) {
            BONotFoundException be = (BONotFoundException) e;
            result = Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorTO(be.getCategory(), be.getErrorCode(), be.getLocalizedMessage())).build();
        } else if ( e instanceof AppServiceException ) {
            AppServiceException ase = (AppServiceException) e;
            result = Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorTO(ase.getCategory(), ase.getModuleName(), ase.getErrorCode(), ase
                            .getLocalizedMessage())).build();
        } else if ( e instanceof MadzException ) {
            MadzException me = (MadzException) e;
            result = Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorTO(me.getCategory(), me.getErrorCode(), me.getLocalizedMessage())).build();
        } else {
            result = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorTO("SYSTEM_ERROR", "INTERNAL_SERVER_ERROR", e.getLocalizedMessage())).build();
        }
        return result;
    }
}
