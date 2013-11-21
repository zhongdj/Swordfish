package net.madz.lifecycle.meta.instance;

import net.madz.bcel.intercept.UnlockableStack;
import net.madz.lifecycle.LifecycleContext;
import net.madz.lifecycle.meta.MetaObject;
import net.madz.lifecycle.meta.template.RelationConstraintMetadata;
import net.madz.lifecycle.meta.template.StateMetadata;

public interface StateObject<S> extends MetaObject<StateObject<S>, StateMetadata> {

    void verifyValidWhile(Object target, RelationConstraintMetadata[] relation, Object relationInstance, UnlockableStack stack);

    void verifyInboundWhile(Object transitionKey, Object target, String nextState, RelationConstraintMetadata[] relation, Object relationInstance,
            UnlockableStack stack);

    void invokeFromPreStateChangeCallbacks(LifecycleContext<?, S> callbackContext);

    void invokeToPreStateChangeCallbacks(LifecycleContext<?, S> callbackContext);

    void invokeFromPostStateChangeCallbacks(LifecycleContext<?, S> callbackContext);

    void invokeToPostStateChangeCallbacks(LifecycleContext<?, S> callbackContext);
}
