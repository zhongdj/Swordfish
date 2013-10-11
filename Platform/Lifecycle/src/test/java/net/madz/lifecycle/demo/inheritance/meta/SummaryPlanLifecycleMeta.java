package net.madz.lifecycle.demo.inheritance.meta;

import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.TransitionSet;

@StateMachine
public interface SummaryPlanLifecycleMeta {

    @StateSet
    public static class States {

        public static class Ongoing {}

        public static class VolumeLeftEmpty {}

        public static class Done {}
    }

    @TransitionSet
    public static class Transitions {

        public static class CreateServiceOrder {}

        public static class AdjustTotalVolume {}

        public static class ConfirmFinish {}
    }
}
