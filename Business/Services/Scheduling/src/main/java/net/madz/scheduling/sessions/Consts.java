package net.madz.scheduling.sessions;

public interface Consts {
    public static final String MODULE_NAME = "scheduling";
    public static final String BUNDLE_NAME = "scheduling";
    public interface ErrorCodes {

        public static final String SUMMARY_PLAN_ID_INVALID = "100-0001";
        public static final String SUMMARY_PLAN__SHOULD_BE_ONGOING_THAN_VOLUME_EMPTY = "100-0002";
        public static final String SUMMARY_PLAN__SHOULD_BE_ONGOING_THAN_DONE = "100-0003";
        public static final String TRUCK_RESOURCE_ID_INVALID = "100-0004";
        public static final String TRUCK_RESOURCE_NOT_IN_IDLE_OR_BUSY_STATE = "100-0005";
        public static final String MIXING_PLANT_RESOURCE_ID_INVALID = "100-0006";
        public static final String MIXING_PLANT_RESOURCE_INOT_IN_IDLE_OR_BUSY_STATE = "100-0007";
        public static final String SERVER_INTENAL_ERROR = null;
    }
}
