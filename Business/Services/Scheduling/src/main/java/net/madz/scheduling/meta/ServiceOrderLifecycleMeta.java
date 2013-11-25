package net.madz.scheduling.meta;

import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.relation.ErrorMessage;
import net.madz.lifecycle.annotations.relation.InboundWhile;
import net.madz.lifecycle.annotations.relation.InboundWhiles;
import net.madz.lifecycle.annotations.relation.Parent;
import net.madz.lifecycle.annotations.relation.RelateTo;
import net.madz.lifecycle.annotations.relation.RelationSet;
import net.madz.scheduling.meta.ServiceOrderLifecycleMeta.Relations.ConcreteTruckResource;
import net.madz.scheduling.meta.ServiceOrderLifecycleMeta.Relations.PlantResource;
import net.madz.scheduling.meta.ServiceOrderLifecycleMeta.Relations.SummaryPlan;
import net.madz.scheduling.sessions.Consts;
import net.madz.scheduling.sessions.Consts.ErrorCodes;

@StateMachine
public interface ServiceOrderLifecycleMeta extends OrderLifecycleMeta {

    @StateSet
    public static class States extends OrderLifecycleMeta.States {

        @InboundWhiles({
                @InboundWhile(relation = SummaryPlan.class, on = { ServiceSummaryPlanLifecycleMeta.States.Ongoing.class }, otherwise = {
                        @ErrorMessage(states = { ServiceSummaryPlanLifecycleMeta.States.VolumeLeftEmpty.class }, bundle = Consts.BUNDLE_NAME,
                                code = ErrorCodes.SUMMARY_PLAN__SHOULD_BE_ONGOING_THAN_VOLUME_EMPTY),
                        @ErrorMessage(states = { ServiceSummaryPlanLifecycleMeta.States.Done.class }, bundle = Consts.BUNDLE_NAME,
                                code = ErrorCodes.SUMMARY_PLAN__SHOULD_BE_ONGOING_THAN_DONE) }),
                @InboundWhile(relation = PlantResource.class,
                        on = { PlantResourceLifecycleMeta.States.Idle.class, PlantResourceLifecycleMeta.States.Busy.class }, otherwise = { @ErrorMessage(
                                bundle = Consts.BUNDLE_NAME, code = ErrorCodes.MIXING_PLANT_RESOURCE_INOT_IN_IDLE_OR_BUSY_STATE,
                                states = { PlantResourceLifecycleMeta.States.Maintaining.class }) }),
                @InboundWhile(relation = ConcreteTruckResource.class, on = { ConcreteTruckResourceLifecycleMeta.States.Idle.class,
                        ConcreteTruckResourceLifecycleMeta.States.Busy.class }, otherwise = { @ErrorMessage(bundle = Consts.BUNDLE_NAME,
                        code = ErrorCodes.TRUCK_RESOURCE_NOT_IN_IDLE_OR_BUSY_STATE, states = { ConcreteTruckResourceLifecycleMeta.States.Detached.class }) }) })
        public static class Ongoing extends OrderLifecycleMeta.States.Ongoing {}
    }
    @TransitionSet
    public static class Transitions extends OrderLifecycleMeta.Transitions {}
    @RelationSet
    public static class Relations {

        @Parent
        @RelateTo(ServiceSummaryPlanLifecycleMeta.class)
        public static class SummaryPlan {}
        @RelateTo(ConcreteTruckResourceLifecycleMeta.class)
        public static class ConcreteTruckResource {}
        @RelateTo(PlantResourceLifecycleMeta.class)
        public static class PlantResource {}
    }
}
