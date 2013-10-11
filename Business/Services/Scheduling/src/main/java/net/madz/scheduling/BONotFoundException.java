package net.madz.scheduling;

import net.madz.core.exceptions.AppServiceException;

public class BONotFoundException extends AppServiceException {

    private static final long serialVersionUID = 8879433291459196010L;

    public BONotFoundException(Class<?> cls, String bundle, String errorCode, String[] messageVars, Throwable cause) {
        super(cls, bundle, bundle, errorCode, messageVars, cause);
    }

    public BONotFoundException(Class<?> cls, String bundle, String errorCode, Throwable cause) {
        super(cls, bundle, bundle, errorCode, cause);
    }

    public BONotFoundException(Class<?> cls, String bundle, String errorCode) {
        super(cls, bundle, bundle, errorCode);
    }
}
