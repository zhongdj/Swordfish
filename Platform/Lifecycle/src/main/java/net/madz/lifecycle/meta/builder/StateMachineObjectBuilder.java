package net.madz.lifecycle.meta.builder;

import net.madz.lifecycle.meta.instance.StateMachineObject;
import net.madz.lifecycle.meta.template.LifecycleMetaRegistry;

public interface StateMachineObjectBuilder extends AnnotationMetaBuilder<StateMachineObject, StateMachineObject>, StateMachineObject {

    void setRegistry(LifecycleMetaRegistry registry);
}
