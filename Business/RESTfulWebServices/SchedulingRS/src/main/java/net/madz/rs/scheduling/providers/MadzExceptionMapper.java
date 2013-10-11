package net.madz.rs.scheduling.providers;

import java.io.Serializable;

import javax.ejb.EJBException;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import net.madz.utils.MadzException;

@Provider
public class MadzExceptionMapper implements ExceptionMapper<EJBException> {

    @Override
    public Response toResponse(EJBException e) {
        final Response result;
        if ( e.getCause() instanceof EntityNotFoundException ) {
            result = null;
        } else if ( e.getCause() instanceof MadzException ) {
            MadzException me = (MadzException) e.getCause();
            result = Response.status(Response.Status.BAD_REQUEST)
                    .entity(new Error(me.getCategory(), me.getErrorCode(), me.getLocalizedMessage())).build();
        } else {
            result = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new Error("SYSTEM_ERROR", "INTERNAL_SERVER_ERROR", e.getLocalizedMessage())).build();
        }
        return result;
    }

    public static class Error implements Serializable {

        private static final long serialVersionUID = -2474784670718130250L;

        private final String category;

        private final String errorCode;

        private final String errorMessage;

        public Error(String category, String errorCode, String errorMessage) {
            super();
            this.category = category;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        public String getCategory() {
            return category;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
