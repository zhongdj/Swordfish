package net.madz.lifecycle.meta.builder;

import net.madz.lifecycle.meta.instance.StateMachineObject;
import net.madz.lifecycle.meta.instance.TransitionObject;

public interface TransitionObjectBuilder extends AnnotationMetaBuilder<TransitionObject, StateMachineObject>,
        TransitionObject {}
