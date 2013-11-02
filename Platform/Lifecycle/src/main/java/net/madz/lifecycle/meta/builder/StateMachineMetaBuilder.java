package net.madz.lifecycle.meta.builder;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.lifecycle.meta.template.StateMetadata;
import net.madz.verification.VerificationException;

public interface StateMachineMetaBuilder extends AnnotationMetaBuilder<StateMachineMetaBuilder, StateMachineMetaBuilder>,
        StateMachineMetadata {

    void setRegistry(AbsStateMachineRegistry registry);

    void setComposite(boolean b);

    void setOwningState(StateMetadata stateMetaBuilderImpl);

    StateMachineMetadata getRelatedStateMachine(Class<?> relationClass);

    boolean hasTransition(Object obj);

    StateMachineMetaBuilder[] getCompositeStateMachines();
    
    AbsStateMachineRegistry getRegistry();

    StateMachineMetadata loadStateMachineMetadata(Class<?> stateMachineClass) throws VerificationException;
    
}
