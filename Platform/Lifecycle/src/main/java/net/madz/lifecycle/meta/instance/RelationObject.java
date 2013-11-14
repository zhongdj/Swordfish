package net.madz.lifecycle.meta.instance;

import net.madz.lifecycle.meta.MetaObject;
import net.madz.lifecycle.meta.template.RelationConstraintMetadata;

public interface RelationObject extends MetaObject<RelationObject, RelationConstraintMetadata> {

    StateMachineObject getRelatedStateMachine();

    StateObject[] getRelatedStateSet();
}
