package net.madz.lifecycle.meta.template;

import net.madz.lifecycle.meta.Template;
import net.madz.lifecycle.meta.instance.RelationInst;
import net.madz.meta.MetaData;

public interface RelationMetadata extends MetaData, Template<RelationInst> {

    StateMachineMetadata getRelatedStateMachine();

    StateMetadata[] getRelatedStateSet();
}
