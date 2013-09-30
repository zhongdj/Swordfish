package net.madz.lifecycle.demo.relational;

import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;

@StateMachine
public interface IPlantResourceLifecycle {

    @StateSet
    static class States {

        static class Idle {}

        static class Busy {}

        static class Maintaining {}
    }
}
