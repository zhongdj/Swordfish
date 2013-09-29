Lifecycle project provides life cycle definition in a declarative style, and it focus on life cycle non functional requirements at following areas:

1. Implicit life cycle service for reactive (business) object.

1.1 Hiding state transition validation from application developer perspective.

Including: 

1.1.1 Stand-alone reactive object state transition validation
1.1.2 Independent reactive object state transition validation
1.1.2.1 Child reactive object state transition validation in a deep hierarchical business model, whose lifecycle is totally covered by parent object.
1.1.2.2 Relative reactive objects state transition validation, whose lifecycle is dependent on some other object.

1.2 Hiding setting business object's state indicator operations from both business object client and application developer perspective.
1.2.1 setting state for Stand-alone reactive object
1.2.2 setting state for Independent reactive object and avoid concurrency issues on related objects.
1.2.2.1 setting state for Child reactive object state transition with touch all parent objects within Optimistic Lock context(MAYBE in Enterprise app)
1.2.2.2 setting state for Child reactive object state transition with touch all parent objects within Pessimistic Lock context(MAYBE in Mobile app) 
1.2.2.3 setting state for relative reactive object state transition with touch all parent objects within Optimistic Lock context(MAYBE in Enterprise app)
1.2.2.4 setting state for relative reactive object state transition with touch all parent objects within Pessimistic Lock context(MAYBE in Mobile app)

2. Implicit life cycle service for long time recoverable process (Reactive object) with transient illegal state fix and error handling, such as download process.
2.1 Provide a serials of annotations to modeling process states, such as category them into @Initial, @Running, @Stopped, @End groups, 
    to differentiate key state of the process, for example, before all the service started, a @Running process must be a invalid state which needs to be fixed (corrupted) 
2.2 Provide a serials of annotations to modeling process transitions, such as @Corrupt, @Fail, @Redo, @Recover, @Timeout, to provide further operation to the process, 
    such as the invalid process mentioned above needs to be applied with @Corrupt operation.

3. Provide life cycle events from reactive object's state change.

4. Provide life cycle intercepts during reactive object's prior or post state changes happens.


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                                      Quick Look: Stand-alone Reactive Object
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@StateMachine(states = @StateSet(IServiceOrder.States.class),
        transitions = @TransitionSet(IServiceOrder.Transitions.class))
public interface IServiceOrder {

    @StateIndicator("serviceOrderState")
    static class States {
        @Initial
        static class Created {}
        static class Scheduled {}
        static class Ongoing {}
        @End
        static class Finished {}
    }

    static class Transitions {

        static class Schedule {}

        static class Start {}

        static class Finish {}
    }

    @Transition(Schedule.class)
    void allocateResources(IMixingPlantResource plantResource, IConcreteTruckResource truckResource, double volume);

    @Transition(Start.class)
    void confirmStart();

    @Transition(Finish.class)
    void confirmFinish();
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                                      Quick Look: Dependent Reactive Object
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@StateMachine(states = @StateSet(IPlantScheduleOrder.States.class),
              transitions = @TransitionSet(IPlantScheduleOrder.Transitions.class)
              parentOn = IServiceOrder.class)
public interface IPlantScheduleOrder  {

    @StateIndicator("plantScheduleOrderState")
    static class States {

        @Initial
        @InboundWhile(relation="serviceOrder", on=IServiceOrder.States.Scheduled.class)
        // Default @ValidWhile(relation="serviceOrder", on = {IServiceOrder.States.Scheduled.class})
        @Functions({ @Function(transition = Start.class, value = Working.class) })
        static class Created {}

        @InboundWhile(relation="serviceOrder", on={IServiceOrder.States.Ongoing.class})
        // Default @ValidWhile(IServiceOrder.States.Ongoing.class)
        @Functions({ @Function(transition = Finish.class, value = Done.class) })
        static class Working {}

        @End
        @InboundWhile(relation="serviceOrder", on={IServiceOrder.States.Ongoing.class})
        @ValidWhile({ IServiceOrder.States.Ongoing.class, IServiceOrder.States.Finished.class })
        //Default @Functions({})
        static class Done {}
    }

    static class Transitions {

        static class Start {}

        static class Finish {}
    }

    @Transition(Transitions.Start.class)
    void doStartPlantOrder();

    @Transition(Transitions.Finish.class)
    void doFinishPlantOrder();
    
    IServiceOrder getServiceOrder();

}


