package net.madz.lifecycle;

public class LifecycleCommonErrors {

    /**
     * @param {0} transition simple name
     * @param {1} state name
     * @param {2} target object
     */
    public static final String ILLEGAL_TRANSITION_ON_STATE = "002-9000";
    /**
     * @param {0} target object
     * @param {1} target object's state
     * @param {2} relation object
     * @param {3} relation state
     * @param {4} relation definition
     */
    public static final String STATE_INVALID = "002-9001";
    public static final String BUNDLE = "lifecyle_common";

    private LifecycleCommonErrors() {}
}
