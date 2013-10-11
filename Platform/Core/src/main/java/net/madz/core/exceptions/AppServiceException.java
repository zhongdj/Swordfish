package net.madz.core.exceptions;

import net.madz.utils.MadzException;


public class AppServiceException extends MadzException {

    private static final String APP_SERVICE = "AppService";
    private static final long serialVersionUID = 2149613886047906933L;

    public AppServiceException(String bundle, String errorCode, String[] messageVars, Throwable cause) {
        super(bundle, errorCode, messageVars, cause);
    }

    public AppServiceException(String bundle, String errorCode, String[] messageVars) {
        super(bundle, errorCode, messageVars);
    }

    public AppServiceException(String bundle, String errorCode, Throwable cause) {
        super(bundle, errorCode, cause);
    }

    public AppServiceException(String bundle, String errorCode) {
        super(bundle, errorCode);
    }

    @Override
    public String getCategory() {
        return APP_SERVICE;
    }
}
