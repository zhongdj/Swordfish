package net.madz.lifecycle.meta.template;

import net.madz.common.Dumpable;
import net.madz.lifecycle.meta.Recoverable;
import net.madz.lifecycle.meta.Template;
import net.madz.lifecycle.meta.instance.StateMachineInst;
import net.madz.meta.FlavorMetaData;
import net.madz.meta.MetaData;
import net.madz.verification.VerificationException;

public interface StateMachineMetadata extends MetaData, FlavorMetaData<MetaData>, Dumpable, Recoverable,
        Template<StateMachineInst> {

    /* ///////////////////////////////////////////////////////////// */
    /* // State Machine Relation with other State Machine Methods // */
    /* ///////////////////////////////////////////////////////////// */
    boolean hasSuper();

    StateMachineMetadata getSuperStateMachine();

    boolean hasParent();

    StateMachineMetadata getParent();

    boolean hasRelations();

    StateMachineMetadata[] getRelatedStateMachineMetadata();

    boolean hasRelation(Class<?> relationClass);

    StateMachineMetadata getRelatedStateMachine(Class<?> relationClass);

    /* //////////////////////////////////////////////////// */
    /* /////////////// State Related Methods ////////////// */
    /* //////////////////////////////////////////////////// */
    StateMetadata[] getDeclaredStateSet();

    StateMetadata getDeclaredState(Object stateKey);

    /**
     * @return all states in current StateMachine, current StateMachine's
     *         composite StateMachine, super StateMachine, super StateMachine's
     *         composite StateMachine.
     */
    StateMetadata[] getAllStates();

    /**
     * @param stateKey
     * @return state in allStates by specified stateKey.
     */
    StateMetadata getState(Object stateKey);

    StateMetadata getInitialState();

    StateMetadata[] getFinalStates();

    /* //////////////////////////////////////////////////// */
    /* ///////////// Transtion Related Methods //////////// */
    /* //////////////////////////////////////////////////// */
    TransitionMetadata[] getDeclaredTransitionSet();

    // TransitionMetadata[] getSuperTransitionSet();
    TransitionMetadata getDeclaredTransition(Object transitionKey);

    /**
     * @return transitions in current StateMachine, current StateMachine's
     *         CompositeStateMachine, super StateMachines, super
     *         StateMachines'composite
     *         StateMachines.
     */
    TransitionMetadata[] getAllTransitions();

    /**
     * @param transitionKey
     * @return transition in allTransitionSet by specified transitionKey
     */
    TransitionMetadata getTransition(Object transitionKey);

    TransitionMetadata getStateSynchronizationTransition();

    /**
     * @param clazz
     *            defined with @LifecycleMeta, and with @Transition
     *            , @StateIndicator, @Relation.
     * 
     * @return a concrete instance of StateMachineMetadata, whose abstract
     *         part is concreted by the clazz param.
     * @throws VerificationException
     */
    @Override
    StateMachineInst newInstance(Class<?> clazz) throws VerificationException;

    /* //////////////////////////////////////////////////// */
    /* //////// Methods For Composite State Machine /////// */
    /* //////////////////////////////////////////////////// */
    boolean isComposite();

    /**
     * @return a state machine template, in whose state defining this state
     *         machine
     */
    StateMachineMetadata getOwningStateMachine();

    StateMetadata getCompositeState();

    StateMetadata[] getShortcutStateSet();

    StateMachineMetadata[] getCompositeStateMachines();
}
