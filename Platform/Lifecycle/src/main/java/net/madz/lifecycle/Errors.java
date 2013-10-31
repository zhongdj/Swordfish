package net.madz.lifecycle;

public interface Errors {

    public static final String SYNTAX_ERROR_BUNDLE = "syntax_error";
    public static final String REGISTERED_META_ERROR = "002-1000";
    // StateMachine
    public static final String STATEMACHINE_SUPER_MUST_BE_STATEMACHINE = "002-2100";
    public static final String STATEMACHINE_HAS_ONLY_ONE_SUPER_INTERFACE = "002-2101";
    public static final String STATEMACHINE_CLASS_WITHOUT_ANNOTATION = "002-2102";
    public static final String STATEMACHINE_WITHOUT_STATESET = "002-2201";
    public static final String STATEMACHINE_MULTIPLE_STATESET = "002-2202";
    public static final String STATEMACHINE_WITHOUT_TRANSITIONSET = "002-2203";
    public static final String STATEMACHINE_MULTIPLE_TRANSITIONSET = "002-2204";
    public static final String STATEMACHINE_WITHOUT_INNER_CLASSES_OR_INTERFACES = "002-2205";
    public static final String STATEMACHINE_MULTIPLE_CONDITIONSET = "002-2206";
    // StateSet
    public static final String STATESET_WITHOUT_STATE = "002-2300";
    public static final String STATESET_WITHOUT_INITAL_STATE = "002-2400";
    public static final String STATESET_MULTIPLE_INITAL_STATES = "002-2401";
    public static final String STATESET_WITHOUT_FINAL_STATE = "002-2500";
    // TransitionSet
    public static final String TRANSITIONSET_WITHOUT_TRANSITION = "002-2501";
    // State's Function
    public static final String FUNCTION_INVALID_TRANSITION_REFERENCE = "002-2610";
    public static final String FUNCTION_CONDITIONAL_TRANSITION_WITHOUT_CONDITION = "002-2611";
    public static final String FUNCTION_TRANSITION_REFERENCE_BEYOND_COMPOSITE_STATE_SCOPE = "002-2612";
    public static final String FUNCTION_TRANSITION_MUST_BE_NOT_ON_END_STATE = "002-2613";
    public static final String FUNCTION_WITH_EMPTY_STATE_CANDIDATES = "002-2614";
    public static final String STATE_NON_FINAL_WITHOUT_FUNCTIONS = "002-2615";
    public static final String FUNCTION_NEXT_STATESET_OF_FUNCTION_INVALID = "002-2700";
    // State's Shortcut
    public static final String SHORT_CUT_INVALID = "002-2800";
    public static final String COMPOSITE_STATEMACHINE_SHORTCUT_WITHOUT_END = "002-2801";
    public static final String COMPOSITE_STATEMACHINE_FINAL_STATE_WITHOUT_SHORTCUT = "002-2802";
    public static final String COMPOSITE_STATEMACHINE_SHORTCUT_STATE_INVALID = "002-2803";
    // State's Relation
    public static final String RELATION_ATTRIBUTE_OF_INBOUNDWHILE_INVALID = "002-2911";
    public static final String ON_ATTRIBUTE_OF_INBOUNDWHILE_INVALID = "002-2912";
    public static final String OTHERWISE_ATTRIBUTE_OF_INBOUNDWHILE_INVALID = "002-2913";
    public static final String RELATION_ATTRIBUTE_OF_VALIDWHILE_INVALID = "002-2921";
    public static final String ON_ATTRIBUTE_OF_VALIDWHILE_INVALID = "002-2922";
    // LifecycleMeta
    public static final String LM_MUST_CONCRETE_ALL_TRANSITIONS = "002-3210";
    public static final String LM_TRANSITION_METHOD_WITH_OUTBOUNDED_TRANSITION = "002-3211";
    public static final String LM_MUST_CONCRETE_ALL_RELATIONS = "002-3220";
    public static final String LM_MUST_CONCRETE_ALL_CONDITIONS = "002-3230";
    public static final String LM_MUST_HAVE_STATEINDICATOR = "002-3300";
}
