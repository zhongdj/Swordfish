package net.madz.core.exceptions;

import net.madz.utils.MadzException;

public class AppServiceException extends MadzException {

    private static final String APP_SERVICE = "APPLICATION_SERVICE";

    private static final long serialVersionUID = 2149613886047906933L;

    private final String moduleName;

    public AppServiceException(Class<?> cls, String moduleName, String bundle, String errorCode, String[] messageVars,
            Throwable cause) {
        super(cls, bundle, errorCode, messageVars, cause);
        this.moduleName = moduleName;
    }

    public AppServiceException(Class<?> cls, String moduleName, String bundle, String errorCode) {
        super(cls, bundle, errorCode);
        this.moduleName = moduleName;
    }

    public AppServiceException(Class<?> cls, String moduleName, String bundle, String errorCode, Throwable cause) {
        super(cls, bundle, errorCode, cause);
        this.moduleName = moduleName;
    }

    @Override
    public String getCategory() {
        return moduleName == null ? APP_SERVICE : moduleName;
    }
}
