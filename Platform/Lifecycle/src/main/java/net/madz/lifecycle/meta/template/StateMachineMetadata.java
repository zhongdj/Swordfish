package net.madz.lifecycle.meta.template;

import net.madz.common.Dumpable;
import net.madz.lifecycle.meta.Recoverable;
import net.madz.lifecycle.meta.Template;
import net.madz.lifecycle.meta.instance.StateMachineInst;
import net.madz.meta.FlavorMetaData;
import net.madz.meta.MetaData;

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

    /* //////////////////////////////////////////////////// */
    /* /////////////// State Related Methods ////////////// */
    /* //////////////////////////////////////////////////// */
    StateMetadata[] getStateSet();

    StateMetadata getState(Object stateKey);

    StateMetadata getInitialState();

    StateMetadata[] getFinalStates();

    /* //////////////////////////////////////////////////// */
    /* ///////////// Transtion Related Methods //////////// */
    /* //////////////////////////////////////////////////// */
    TransitionMetadata[] getTransitionSet();

    // TransitionMetadata[] getSuperTransitionSet();
    TransitionMetadata getTransition(Object transitionKey);

    TransitionMetadata getStateSynchronizationTransition();

    /**
     * @param clazz
     *            defined with @LifecycleMeta, and with @Transition
     *            , @StateIndicator, @Relation.
     * 
     * @return a concrete instance of StateMachineMetadata, whose abstract
     *         part is concreted by the clazz param.
     */
    @Override
    StateMachineInst newInstance(Class<?> clazz);
    
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
}
