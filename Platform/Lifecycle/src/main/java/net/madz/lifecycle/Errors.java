package net.madz.lifecycle;

public interface Errors {
    public static final String REGISTERED_META_ERROR = "002-1000";
    public static final String STATEMACHINE_SUPER_MUST_BE_STATEMACHINE = "002-2100";
    public static final String STATEMACHINE_WITHOUT_STATESET = "002-2201";
    public static final String STATEMACHINE_MULTIPLE_STATESET = "002-2202";
    public static final String STATEMACHINE_WITHOUT_TRANSITIONSET = "002-2203";
    public static final String STATEMACHINE_MULTIPLE_TRANSITIONSET = "002-2204";
    public static final String STATESET_WITHOUT_STATE = "002-2300";
    public static final String STATESET_WITHOUT_INITAL_STATE = "002-2400";
    public static final String STATESET_MULTIPLE_INITAL_STATES = "002-2401";
    public static final String STATESET_WITHOUT_FINAL_STATE = "002-2500";
    public static final String INVALID_TRANSITION_REFERENCE = "002-2610";
    public static final String CONDITIONAL_TRANSITION_WITHOUT_CONDITION = "002-2611";
    public static final String TRANSITION_REFERENCE_BEYOND_COMPOSITE_STATE_SCOPE = "002-2612";
}
