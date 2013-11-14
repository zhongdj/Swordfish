package net.madz.lifecycle.meta.instance;

import net.madz.lifecycle.meta.MetaObject;
import net.madz.lifecycle.meta.instance.StateMachineObject.ReadAccessor;
import net.madz.lifecycle.meta.template.RelationConstraintMetadata;
import net.madz.lifecycle.meta.template.StateMetadata;

public interface StateObject extends MetaObject<StateObject, StateMetadata> {

    void verifyValidWhile(Object target, RelationConstraintMetadata[] relation, ReadAccessor<?> evaluator);

    void verifyInboundWhile(Object transitionKey, Object target, String nextState, RelationConstraintMetadata[] relation, ReadAccessor<?> evaluator);
}
