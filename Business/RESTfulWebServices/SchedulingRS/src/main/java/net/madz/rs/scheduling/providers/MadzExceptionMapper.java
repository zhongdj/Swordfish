package net.madz.rs.scheduling.providers;

import java.io.Serializable;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

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
                    .entity(new Error(be.getCategory(), be.getErrorCode(), be.getLocalizedMessage())).build();
        } else if ( e instanceof MadzException ) {
            MadzException me = (MadzException) e;
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

        private String category;

        private String errorCode;

        private String errorMessage;

        public Error() {
            super();
        }

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

        public void setCategory(String category) {
            this.category = category;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}
