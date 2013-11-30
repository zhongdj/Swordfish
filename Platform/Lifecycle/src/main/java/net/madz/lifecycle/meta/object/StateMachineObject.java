package net.madz.lifecycle.meta.object;

import net.madz.bcel.intercept.LifecycleInterceptContext;
import net.madz.bcel.intercept.UnlockableStack;
import net.madz.lifecycle.LifecycleLockStrategry;
import net.madz.lifecycle.meta.MetaObject;
import net.madz.lifecycle.meta.builder.impl.CallbackObject;
import net.madz.lifecycle.meta.type.StateMachineMetadata;

public interface StateMachineObject<S> extends MetaObject<StateMachineObject<S>, StateMachineMetadata> {

    void doInterceptBefore(LifecycleInterceptContext context);

    void doInterceptAfter(LifecycleInterceptContext context);

    void doInterceptException(LifecycleInterceptContext lifecycleContext);

    StateObject<S> getState(Object stateKey);

    String evaluateState(Object target);

    LifecycleLockStrategry getLifecycleLockStrategy();

    void validateValidWhiles(Object target, UnlockableStack stack);

    boolean isLockEnabled();

    void addSpecificPreStateChangeCallbackObject(CallbackObject item);

    void addCommonPreStateChangeCallbackObject(CallbackObject item);

    void addSpecificPostStateChangeCallbackObject(CallbackObject item);

    void addCommonPostStateChangeCallbackObject(CallbackObject item);
}