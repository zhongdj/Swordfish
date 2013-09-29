package net.madz.lifecycle;

import net.madz.lifecycle.annotations.typed.TypedStateContext;

public class IllegalStateChangeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public static final int ILLEAGLE_TRANSITION = 10000;
    public static final int TRANSIT_FROM_FINAL_STATE = 10001;

    private final TypedStateContext<?, ?> context;
    private final int errorCode;

    public IllegalStateChangeException(TypedStateContext<?, ?> context) {
        this(context, ILLEAGLE_TRANSITION);
    }

    public IllegalStateChangeException(TypedStateContext<?, ?> context, int errorCode) {

        try {
            this.context = (TypedStateContext<?, ?>) context.clone();
            this.errorCode = errorCode;
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }

    public TypedStateContext<?, ?> getContext() {
        return context;
    }

    public int getErrorCode() {
        return errorCode;
    }

}
