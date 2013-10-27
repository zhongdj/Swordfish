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
    public static final String NEXT_STATESET_OF_FUNCTION_INVALID = "002-2700";
    public static final String SHORT_CUT_INVALID = "002-2800";
    public static final String RELATION_ATTRIBUTE_OF_INBOUNDWHILE_INVALID = "002-2911";
    public static final String ON_ATTRIBUTE_OF_INBOUNDWHILE_INVALID = "002-2912";
    public static final String OTHERWISE_ATTRIBUTE_OF_INBOUNDWHILE_INVALID = "002-2913";
    public static final String RELATION_ATTRIBUTE_OF_VALIDWHILE_INVALID = "002-2921";
    public static final String ON_ATTRIBUTE_OF_VALIDWHILE_INVALID = "002-2922";
    public static final String LM_VALUE_MUST_BE_STATEMACHINE = "002-3100";
    public static final String LM_MUST_CONCRETE_ALL_TRANSITIONS = "002-3201";
    public static final String LM_MUST_CONCRETE_ALL_RELATIONS = "002-3202";
    public static final String LM_MUST_CONCRETE_ALL_CONDITIONS = "002-3203";
    public static final String LM_MUST_HAVE_STATEINDICATOR = "002-3300";
}
