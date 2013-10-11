package net.madz.scheduling;

import net.madz.core.exceptions.AppServiceException;

public class BONotFoundException extends AppServiceException {

    private static final long serialVersionUID = 8879433291459196010L;

    public BONotFoundException(String bundle, String errorCode) {
        super(bundle, errorCode);
    }

    public BONotFoundException(String bundle, String errorCode, String[] messageVars, Throwable cause) {
        super(bundle, errorCode, messageVars, cause);
    }

    public BONotFoundException(String bundle, String errorCode, String[] messageVars) {
        super(bundle, errorCode, messageVars);
    }

    public BONotFoundException(String bundle, String errorCode, Throwable cause) {
        super(bundle, errorCode, cause);
    }
}
