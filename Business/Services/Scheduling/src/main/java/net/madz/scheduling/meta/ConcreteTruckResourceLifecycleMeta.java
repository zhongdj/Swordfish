package net.madz.scheduling.meta;

import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.TransitionSet;

@StateMachine
public interface ConcreteTruckResourceLifecycleMeta extends SchedulableResourceLifecycleMeta {

    @StateSet
    public static class States extends SchedulableResourceLifecycleMeta.States {}
    @TransitionSet
    public static class Transitions extends SchedulableResourceLifecycleMeta.Transitions {}
}
