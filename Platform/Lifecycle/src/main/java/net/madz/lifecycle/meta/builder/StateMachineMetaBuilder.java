package net.madz.lifecycle.meta.builder;

import net.madz.lifecycle.meta.template.LifecycleMetaRegistry;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.lifecycle.meta.template.StateMetadata;

public interface StateMachineMetaBuilder extends AnnotationMetaBuilder<StateMachineMetadata, StateMachineMetadata>,
        StateMachineMetadata {

    void setRegistry(LifecycleMetaRegistry registry);

    void setComposite(boolean b);

    void setOwningState(StateMetadata stateMetaBuilderImpl);

    StateMachineMetadata getRelatedStateMachine(Class<?> relationClass);

    boolean hasTransition(Object obj);

    StateMachineMetaBuilder[] getCompositeStateMachines();

    LifecycleMetaRegistry getRegistry();
}
