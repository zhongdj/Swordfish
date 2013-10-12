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

@StateMachine
public class ServiceOrderLifecycleMeta implements OrderLifecycleMeta {

    @StateSet
    public static class States extends OrderLifecycleMeta.States {

        @InboundWhiles(value = { @InboundWhile(on = { SummaryPlanLifecycleMeta.States.Ongoing.class }, relation = SummaryPlan.class, otherwise = {
                @ErrorMessage(states = { SummaryPlanLifecycleMeta.States.VolumeLeftEmpty.class }, bundle = "scheduling", code = "100-0002"),
                @ErrorMessage(states = { SummaryPlanLifecycleMeta.States.Done.class }, bundle = "scheduling", code = "100-0003") }) })
        public static class Created extends OrderLifecycleMeta.States.Created {
        }
        @InboundWhiles({
            @InboundWhile(relation = PlantResource.class, on = { PlantResourceLifecycleMeta.States.Idle.class,
                    PlantResourceLifecycleMeta.States.Busy.class }),
            @InboundWhile(relation = ConcreteTruckResource.class, on = {
                    ConcreteTruckResourceLifecycleMeta.States.Idle.class,
                    ConcreteTruckResourceLifecycleMeta.States.Busy.class }) })
        public static class Queued extends OrderLifecycleMeta.States.Queued {
        }
    }

    @TransitionSet
    public static class Transitions extends OrderLifecycleMeta.Transitions {
    }

    @RelationSet
    public static class Relations {

        public static class SummaryPlan {
        }

        public static class ConcreteTruckResource {
        }

        public static class PlantResource {
        }
    }
}
