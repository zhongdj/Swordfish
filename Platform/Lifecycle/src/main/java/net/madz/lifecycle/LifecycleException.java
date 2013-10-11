package net.madz.lifecycle;

import net.madz.utils.MadzException;

public class LifecycleException extends MadzException {

    private static final String LIFECYCLE = "Lifecycle";

    public LifecycleException(String bundle, String errorCode, String[] messageVars, Throwable cause) {
        super(bundle, errorCode, messageVars, cause);
    }

    public LifecycleException(String bundle, String errorCode, String[] messageVars) {
        super(bundle, errorCode, messageVars);
    }

    public LifecycleException(String bundle, String errorCode, Throwable cause) {
        super(bundle, errorCode, cause);
    }

    public LifecycleException(String bundle, String errorCode) {
        super(bundle, errorCode);
    }

    @Override
    public String getCategory() {
        return LIFECYCLE;
    }
}
