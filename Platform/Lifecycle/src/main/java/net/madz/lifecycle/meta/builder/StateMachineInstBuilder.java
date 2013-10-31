package net.madz.lifecycle.meta.builder;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.meta.instance.StateMachineInst;
import net.madz.meta.MetaDataBuilder;


public interface StateMachineInstBuilder extends MetaDataBuilder<StateMachineInstBuilder, StateMachineMetaBuilder>, StateMachineInst  {
    
    void setRegistry(AbsStateMachineRegistry registry);
}
