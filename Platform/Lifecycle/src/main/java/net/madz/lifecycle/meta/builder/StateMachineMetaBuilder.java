package net.madz.lifecycle.meta.builder;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.meta.template.StateMachineMetadata;

public interface StateMachineMetaBuilder extends AnnotationMetaBuilder<StateMachineMetaBuilder, StateMachineMetaBuilder>,
        StateMachineMetadata {

    void setRegistry(AbsStateMachineRegistry registry);

    void setComposite(boolean b);

    void setOwningState(StateMetaBuilder stateMetaBuilderImpl);

    StateMachineMetadata getRelatedStateMachine(Class<?> relationClass);

    boolean hasTransition(Object obj);

    StateMachineMetaBuilder[] getCompositeStateMachines();
    
    AbsStateMachineRegistry getRegistry();
    
}
