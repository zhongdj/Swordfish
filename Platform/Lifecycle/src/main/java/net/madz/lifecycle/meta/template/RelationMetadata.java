package net.madz.lifecycle.meta.template;

import net.madz.lifecycle.meta.MetaType;

public interface RelationMetadata extends MetaType<RelationMetadata> {

    StateMachineMetadata getRelateToStateMachine();

    boolean isParent();
}
