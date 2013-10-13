package net.madz.rs.scheduling.providers;

import java.io.Serializable;

public class ErrorTO implements Serializable {

    private static final long serialVersionUID = -2474784670718130250L;

    private String category;

    private String moduleName;

    private String errorCode;

    private String errorMessage;

    public ErrorTO() {
        super();
    }

    public ErrorTO(String category, String errorCode, String errorMessage) {
        this(category, "UNKNOWN", errorCode, errorMessage);
    }

    public ErrorTO(String category, String moduleName, String errorCode, String errorMessage) {
        super();
        if ( null != moduleName ) {
            this.moduleName = moduleName;
        }
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

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public String toString() {
        return "ErrorTO [category=" + category + ", moduleName=" + moduleName + ", errorCode=" + errorCode
                + ", errorMessage=" + errorMessage + "]";
    }
}