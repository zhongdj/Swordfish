package net.madz.lifecycle.meta.instance;

import net.madz.bcel.intercept.UnlockableStack;
import net.madz.lifecycle.meta.MetaObject;
import net.madz.lifecycle.meta.template.RelationConstraintMetadata;
import net.madz.lifecycle.meta.template.StateMetadata;

public interface StateObject extends MetaObject<StateObject, StateMetadata> {

    void verifyValidWhile(Object target, RelationConstraintMetadata[] relation, Object relationInstance, UnlockableStack stack);

    void verifyInboundWhile(Object transitionKey, Object target, String nextState, RelationConstraintMetadata[] relation, Object relationInstance, UnlockableStack stack);
}
