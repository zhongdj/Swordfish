package net.madz.lifecycle.meta.builder;

import net.madz.lifecycle.meta.instance.ConditionObject;

public interface ConditionObjectBuilder extends ConditionObject,
        AnnotationMetaBuilder<ConditionObjectBuilder, StateMachineObjectBuilder> {}
