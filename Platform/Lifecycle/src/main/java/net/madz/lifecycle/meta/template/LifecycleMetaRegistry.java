package net.madz.lifecycle.meta.template;

import net.madz.lifecycle.meta.instance.StateMachineObject;
import net.madz.verification.VerificationException;

public interface LifecycleMetaRegistry {

    StateMachineMetadata loadStateMachineMetadata(Class<?> stateClass, StateMachineMetadata parent) throws VerificationException;

    StateMachineObject loadStateMachineObject(Class<?> returnType) throws VerificationException;
}
