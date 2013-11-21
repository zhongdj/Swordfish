package net.madz.lifecycle.meta.builder;

import net.madz.lifecycle.meta.instance.StateMachineObject;
import net.madz.lifecycle.meta.instance.StateObject;

public interface StateObjectBuilder<S> extends AnnotationMetaBuilder<StateObject<S>, StateMachineObject<S>>, StateObject<S> {}
