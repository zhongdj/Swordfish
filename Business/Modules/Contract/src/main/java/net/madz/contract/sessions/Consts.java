package net.madz.contract.sessions;

public interface Consts {

    static String MODULE_NAME = "contract";
    static String BUNDLE_NAME = "contract";

    static interface ErrorCodes {

        static String MIXTURE_ID_NOT_EXIST = "050-001";
        static String MIXTURE_GRADE_NAME_EMPTY = "050-002";
        static String MIXTURE_NOT_FOUND_WITH_GRADE_NAME = "050-003";
        static String ADDITIVE_ID_INVALID = "050-004";
        static String ADDITIVE_NAME_EMPTY = "050-005";
        static String ADDITIVE_NAME_INVALID = "005-006";
        static String POURING_PART_NAME_EMPTY = "005-007";
        static String UNIT_PROJECT_ID_NULL = "005-008";
        static String UNIT_PROJECT_ID_INVALID = "050-009";
        static String SERVER_INTERNAL_ERROR = "050-000";
    }
}
