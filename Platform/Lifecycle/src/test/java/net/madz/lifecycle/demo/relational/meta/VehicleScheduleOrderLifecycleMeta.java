package net.madz.lifecycle.demo.relational.meta;

import net.madz.lifecycle.annotations.StateMachine;

@StateMachine
public interface VehicleScheduleOrderLifecycleMeta {

    static class States {

        static class OnCall {}

        static class Loading {}
    }
}
