package net.madz.lifecycle.meta.builder;

import net.madz.lifecycle.meta.instance.StateMachineObject;
import net.madz.lifecycle.meta.instance.StateObject;

public interface StateObjectBuilder extends AnnotationMetaBuilder<StateObject, StateMachineObject>, StateObject {}
