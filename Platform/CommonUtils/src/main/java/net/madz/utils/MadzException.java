package net.madz.utils;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

public abstract class MadzException extends Exception {

    private static final long serialVersionUID = 163564734666783236L;

    protected final String errorCode;

    protected final String bundle;

    public MadzException(Class<?> cls, String bundle, String errorCode, Throwable cause) {
        super(getBundledMessage(cls, bundle, errorCode), cause);
        this.errorCode = errorCode;
        this.bundle = bundle;
    }

    public MadzException(Class<?> cls, String bundle, String errorCode) {
        super(getBundledMessage(cls, bundle, errorCode));
        this.errorCode = errorCode;
        this.bundle = bundle;
    }

    public MadzException(Class<?> cls, String bundle, String errorCode, String[] messageVars, Throwable cause) {
        super(getBundledMessage(cls, bundle, errorCode, messageVars), cause);
        this.errorCode = errorCode;
        this.bundle = bundle;
    }

    protected static String getBundledMessage(Class<?> cls, String bundle, String errorCode) {
        return getBundledMessage(cls, bundle, errorCode, new String[0]);
    }

    protected static String getBundledMessage(Class<?> cls, String bundle, String errorCode, String[] messageVars) {
        return ResourceBundle.getBundle(bundle, Locale.CHINA, cls.getClassLoader()).getString(errorCode);
    }

    public String getBundle() {
        return bundle;
    }

    public abstract String getCategory();

    public String getErrorCode() {
        return errorCode;
    }

    public static class ResourceBundleImpl extends ResourceBundle {

        @Override
        protected Object handleGetObject(String key) {
            return null;
        }

        @Override
        public Enumeration<String> getKeys() {
            // TODO Auto-generated method stub
            return null;
        }
    }
}