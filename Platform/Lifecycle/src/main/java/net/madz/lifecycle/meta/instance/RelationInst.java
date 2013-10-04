package net.madz.lifecycle.meta.instance;

import net.madz.lifecycle.meta.Instance;
import net.madz.lifecycle.meta.template.RelationMetadata;

public interface RelationInst extends Instance<RelationMetadata> {

    StateMachineInst getRelatedStateMachine();

    StateInst[] getRelatedStateSet();
}
