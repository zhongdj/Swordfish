package net.madz.rs.scheduling.providers;

import java.io.Serializable;

public class ErrorTO implements Serializable {

    private static final long serialVersionUID = -2474784670718130250L;

    private String category;

    private String errorCode;

    private String errorMessage;

    public ErrorTO() {
        super();
    }

    public ErrorTO(String category, String errorCode, String errorMessage) {
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

    @Override
    public String toString() {
        return "ErrorTO [category=" + category + ", errorCode=" + errorCode + ", errorMessage=" + errorMessage + "]";
    }
}