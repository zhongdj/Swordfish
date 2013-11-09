package net.madz.lifecycle.meta.builder;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.meta.instance.StateMachineObject;
import net.madz.meta.MetaDataBuilder;


public interface StateMachineObjectBuilder extends MetaDataBuilder<StateMachineObjectBuilder, StateMachineMetaBuilder>, StateMachineObject  {
    
    void setRegistry(AbsStateMachineRegistry registry);
}
