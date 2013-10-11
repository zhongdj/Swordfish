package net.madz.lifecycle;

public abstract class MadzException extends Exception {

    private static final long serialVersionUID = 163564734666783236L;

    protected final String errorCode;

    protected static String getBundledMessage(String bundle, String errorCode) {
        return getBundledMessage(bundle, errorCode, new String[0]);
    }

    protected static String getBundledMessage(String bundle, String errorCode, String[] messageVars) {
        return null;
    }

    public MadzException(String bundle, String errorCode, String[] messageVars, Throwable cause) {
        super(getBundledMessage(bundle, errorCode, messageVars), cause);
        this.errorCode = errorCode;
    }

    public MadzException(String bundle, String errorCode, String[] messageVars) {
        super(getBundledMessage(bundle, errorCode, messageVars));
        this.errorCode = errorCode;
    }

    public MadzException(String bundle, String errorCode, Throwable cause) {
        super(getBundledMessage(bundle, errorCode), cause);
        this.errorCode = errorCode;
    }

    public MadzException(String bundle, String errorCode) {
        super(getBundledMessage(bundle, errorCode));
        this.errorCode = errorCode;
    }

    public abstract String getCategory();

    public String getErrorCode() {
        return errorCode;
    }
}