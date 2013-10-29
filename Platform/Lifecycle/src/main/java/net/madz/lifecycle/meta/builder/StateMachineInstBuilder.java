package net.madz.lifecycle.meta.builder;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.meta.instance.StateMachineInst;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.meta.MetaDataBuilder;


public interface StateMachineInstBuilder extends MetaDataBuilder<StateMachineInst, StateMachineMetadata>, StateMachineInst  {
    
    void setRegistry(AbsStateMachineRegistry registry);
}
