package net.madz.lifecycle.meta.instance;

import net.madz.lifecycle.meta.Concrete;
import net.madz.lifecycle.meta.template.RelationMetadata;

public interface RelationObject extends Concrete<RelationMetadata> {

    StateMachineObject getRelatedStateMachine();

    StateObject[] getRelatedStateSet();
}
