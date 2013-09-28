package net.madz.lifecycle;

public class IllegalStateChangeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public static final int ILLEAGLE_TRANSITION = 10000;
    public static final int TRANSIT_FROM_FINAL_STATE = 10001;

    private final StateContext<?, ?> context;
    private final int errorCode;

    public IllegalStateChangeException(StateContext<?, ?> context) {
        this(context, ILLEAGLE_TRANSITION);
    }

    public IllegalStateChangeException(StateContext<?, ?> context, int errorCode) {

        try {
            this.context = (StateContext<?, ?>) context.clone();
            this.errorCode = errorCode;
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }

    public StateContext<?, ?> getContext() {
        return context;
    }

    public int getErrorCode() {
        return errorCode;
    }

}
