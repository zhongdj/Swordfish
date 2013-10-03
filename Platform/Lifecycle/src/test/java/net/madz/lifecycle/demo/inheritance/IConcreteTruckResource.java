package net.madz.lifecycle.demo.inheritance;

import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.StateIndicator;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.demo.inheritance.meta.ConcreteTruckResourceLifecycleMeta;

@LifecycleMeta(ConcreteTruckResourceLifecycleMeta.class)
@StateIndicator
//Default with getState
public interface IConcreteTruckResource {

    @Transition
    // default to @Transition(Assign.class) use assign -> Assign
    void assign();

    @Transition(ConcreteTruckResourceLifecycleMeta.Transitions.Release.class)
    void doRelease();

    @Transition
    // default to @Transition(Detach.class) use detach -> Detach
    void detach();
}
