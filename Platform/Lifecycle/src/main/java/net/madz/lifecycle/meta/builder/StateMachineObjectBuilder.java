package net.madz.lifecycle.meta.builder;

import net.madz.lifecycle.meta.instance.StateMachineObject;
import net.madz.lifecycle.meta.template.LifecycleMetaRegistry;
import net.madz.verification.VerificationException;

public interface StateMachineObjectBuilder<S> extends AnnotationMetaBuilder<StateMachineObject<S>, StateMachineObject<S>>, StateMachineObject<S> {

    void setRegistry(LifecycleMetaRegistry registry);

    @Override
    StateMachineObjectBuilder<S> build(Class<?> klass, StateMachineObject<S> parent) throws VerificationException;
}
