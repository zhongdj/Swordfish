package net.madz.lifecycle.demo.inheritance.meta;

import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.state.InboundWhile;
import net.madz.lifecycle.annotations.state.InboundWhiles;
import net.madz.lifecycle.annotations.state.RelationSet;
import net.madz.lifecycle.demo.inheritance.meta.ServiceOrderLifecycleMeta.Relations.ConcreteTruckResource;
import net.madz.lifecycle.demo.inheritance.meta.ServiceOrderLifecycleMeta.Relations.PlantResource;

@StateMachine
public interface ServiceOrderLifecycleMeta extends OrderLifecycleMeta {

    @StateSet
    public class States extends OrderLifecycleMeta.States {

        @InboundWhiles({
                @InboundWhile(relation = PlantResource.class, on = { PlantResourceLifecycleMeta.States.Idle.class,
                        PlantResourceLifecycleMeta.States.Busy.class }),
                @InboundWhile(relation = ConcreteTruckResource.class, on = {
                        ConcreteTruckResourceLifecycleMeta.States.Idle.class,
                        ConcreteTruckResourceLifecycleMeta.States.Busy.class }) })
        public static class Queued extends OrderLifecycleMeta.States.Queued {}
    }

    @TransitionSet
    public class Transitions extends OrderLifecycleMeta.Transitions {}

    @RelationSet
    public static class Relations {

        // default to @Relation("plantResource")
        public static class PlantResource {}

        // default to @Relation("concreteTruckResource")
        public static class ConcreteTruckResource {}
    }
}
