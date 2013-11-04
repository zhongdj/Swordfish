package net.madz.lifecycle.meta.template;

import net.madz.lifecycle.meta.Template;
import net.madz.lifecycle.meta.instance.ErrorMessageObject;
import net.madz.lifecycle.meta.instance.RelationInst;
import net.madz.meta.MetaData;
import net.madz.meta.MetaDataFilterable;

public interface RelationMetadata extends MetaData, Template<RelationInst>, MetaDataFilterable {

    StateMetadata getParent();
    
    StateMachineMetadata getRelatedStateMachine();
    
    StateMetadata[] getOnStates();
    
    ErrorMessageObject[] getErrorMessageObjects();
    
}
