package net.madz.lifecycle.meta.template;

import net.madz.common.Dumpable;
import net.madz.lifecycle.meta.Recoverable;
import net.madz.lifecycle.meta.instance.FunctionMetadata;
import net.madz.meta.FlavorMetaData;
import net.madz.meta.MetaData;

public interface StateMetadata extends MetaData, Recoverable, FlavorMetaData<MetaData>, Dumpable {

    /* ////////////////////////////////////////////////////////////////// */
    /* //////////////////////////Basic Properties /////////////////////// */
    /* ////////////////////////////////////////////////////////////////// */
    StateMachineMetadata getStateMachine();

    String getSimpleName();

    boolean isInitial();

    boolean isFinal();

    /* ////////////////////////////////////////////////////////////////// */
    /* ////////////////////////Transition Related /////////////////////// */
    /* ////////////////////////////////////////////////////////////////// */
    TransitionMetadata[] getPossibleTransitions();

    TransitionMetadata getTransition(Object transitionKey);

    boolean isTransitionValid(Object transitionKey);

    /* ////////////////////////////////////////////////////////////////// */
    /* //////////////////////////Inheritance Part /////////////////////// */
    /* ////////////////////////////////////////////////////////////////// */
    boolean isOverriding();

    StateMetadata getSuperStateMetadata();

    /* ////////////////////////////////////////////////////////////////// */
    /* //////////////////////////Dependency Part //////////////////////// */
    /* ////////////////////////////////////////////////////////////////// */
    boolean hasInboundWhiles();

    /**
     * @return related state dependencies, expected to be used post-state-change
     *         validation
     */
    RelationMetadata[] getInboundWhiles();

    boolean hasValidWhiles();

    /**
     * @return related state dependencies, expected to be used pre-state-change
     *         validation, which will validate the validity of the state. Once
     *         the state is not invalid, transitions will fail until the state
     *         has been fixed by synchronizationTransition.
     * 
     *         And if parent object life cycle exists, then this state should be
     *         valid ONLY in a subset of parent life cycle states, so does the
     *         parent object, the validation will go up along the parent's
     *         parent recursively.
     * 
     */
    RelationMetadata[] getValidWhiles();

    /* ////////////////////////////////////////////////////////////////// */
    /* //////////////////////////Composite State///////////////////////// */
    /* ////////////////////////////////////////////////////////////////// */
    boolean isCompositeState();

    StateMetadata getOwningState();

    StateMachineMetadata getCompositeStateMachine();

    /* For Shortcut State inside a composite state */
    StateMetadata getLinkTo();

    FunctionMetadata[] getDeclaredFunctionMetadata();

    FunctionMetadata getDeclaredFunctionMetadata(Object functionKey);
}
