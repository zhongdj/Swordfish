package net.madz.scheduling.meta;

import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.state.ErrorMessage;
import net.madz.lifecycle.annotations.state.InboundWhile;
import net.madz.lifecycle.annotations.state.InboundWhiles;
import net.madz.lifecycle.annotations.state.RelationSet;
import net.madz.scheduling.meta.ServiceOrderLifecycleMeta.Relations.ConcreteTruckResource;
import net.madz.scheduling.meta.ServiceOrderLifecycleMeta.Relations.PlantResource;
import net.madz.scheduling.meta.ServiceOrderLifecycleMeta.Relations.SummaryPlan;

@StateMachine(parentOn = ServiceSummaryPlanLifecycleMeta.class)
public interface ServiceOrderLifecycleMeta extends OrderLifecycleMeta {

    @StateSet
    public static class States extends OrderLifecycleMeta.States {

        @InboundWhiles({
                @InboundWhile(relation = SummaryPlan.class, on = { ServiceSummaryPlanLifecycleMeta.States.Ongoing.class }, otherwise = {
                        @ErrorMessage(states = { ServiceSummaryPlanLifecycleMeta.States.VolumeLeftEmpty.class }, bundle = "scheduling", code = "100-0002"),
                        @ErrorMessage(states = { ServiceSummaryPlanLifecycleMeta.States.Done.class }, bundle = "scheduling", code = "100-0003") }),
                @InboundWhile(relation = PlantResource.class,
                        on = { PlantResourceLifecycleMeta.States.Idle.class, PlantResourceLifecycleMeta.States.Busy.class }, otherwise = { @ErrorMessage(
                                bundle = "sheduling", code = "100-0007", states = { PlantResourceLifecycleMeta.States.Maintaining.class }) }),
                @InboundWhile(relation = ConcreteTruckResource.class, on = { ConcreteTruckResourceLifecycleMeta.States.Idle.class,
                        ConcreteTruckResourceLifecycleMeta.States.Busy.class }, otherwise = { @ErrorMessage(bundle = "sheduling", code = "100-0005",
                        states = { ConcreteTruckResourceLifecycleMeta.States.Detached.class }) }) })
        public static class Created extends OrderLifecycleMeta.States.Created {}
    }

    @TransitionSet
    public static class Transitions extends OrderLifecycleMeta.Transitions {}

    @RelationSet
    public static class Relations {

        public static class SummaryPlan {}

        public static class ConcreteTruckResource {}

        public static class PlantResource {}
    }
}
