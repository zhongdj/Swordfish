package net.madz.lifecycle.meta.template;

import net.madz.lifecycle.meta.MetaType;
import net.madz.lifecycle.meta.instance.ErrorMessageObject;

public interface RelationMetadata extends MetaType<RelationMetadata> {

    StateMetadata getParent();

    StateMachineMetadata getRelatedStateMachine();

    StateMetadata[] getOnStates();

    ErrorMessageObject[] getErrorMessageObjects();
}
