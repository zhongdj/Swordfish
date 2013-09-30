package net.madz.lifecycle.demo.relational;

import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.Functions;
import net.madz.lifecycle.annotations.StateIndicator;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.action.End;
import net.madz.lifecycle.annotations.state.InboundWhile;
import net.madz.lifecycle.annotations.state.InboundWhiles;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.annotations.state.Relation;
import net.madz.lifecycle.annotations.state.RelationSet;
import net.madz.lifecycle.demo.relational.IServiceOrderLifecycle.Relations.PlantResource;
import net.madz.lifecycle.demo.relational.IServiceOrderLifecycle.Transitions.Cancel;
import net.madz.lifecycle.demo.relational.IServiceOrderLifecycle.Transitions.Finish;
import net.madz.lifecycle.demo.relational.IServiceOrderLifecycle.Transitions.Schedule;
import net.madz.lifecycle.demo.relational.IServiceOrderLifecycle.Transitions.Start;

@StateMachine
public interface IServiceOrderLifecycle {

    @StateSet
    @StateIndicator("serviceOrderState")
    static class States {

        @Initial
        @Function(transition = Schedule.class, value = Scheduled.class)
        @InboundWhiles({
                @InboundWhile(relation = PlantResource.class, on = { IPlantResourceLifecycle.States.Idle.class,
                        IPlantResourceLifecycle.States.Busy.class }),
                @InboundWhile(relation = PlantResource.class, on = { IConcreteTruckResourceLifecycle.States.Idle.class,
                        IConcreteTruckResourceLifecycle.States.Busy.class }) })
        static class Created {}

        @Functions({ @Function(transition = Start.class, value = Ongoing.class),
                @Function(transition = Cancel.class, value = Cancelled.class) })
        static class Scheduled {}

        @Functions({ @Function(transition = Finish.class, value = Finished.class),
                @Function(transition = Cancel.class, value = Cancelled.class) })
        static class Ongoing {}

        @End
        static class Finished {}

        @End
        static class Cancelled {}
    }

    @TransitionSet
    static class Transitions {

        static class Schedule {}

        static class Start {}

        static class Finish {}

        static class Cancel {}
    }

    @RelationSet
    static class Relations {

        @Relation
        // default to @Relation("plantResource")
        static class PlantResource {}

        @Relation
        // default to @Relation("concreteTruckResource")
        static class ConcreteTruckResource {}
    }

    @Transition(Schedule.class)
    void allocateResources(IPlantResourceLifecycle plantResource, IConcreteTruckResourceLifecycle truckResource);

    @Transition(Start.class)
    void confirmStart();

    @Transition(Finish.class)
    void confirmFinish();
}