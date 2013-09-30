package net.madz.lifecycle.demo.relational;

import net.madz.lifecycle.annotations.StateMachine;

@StateMachine
public interface IVehicleScheduleOrderLifecycle {

    static class States {

        
        
        static class OnCall {}

        static class Loading {}
    }
}
