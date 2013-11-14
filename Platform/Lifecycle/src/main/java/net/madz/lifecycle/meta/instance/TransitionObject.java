package net.madz.lifecycle.meta.instance;

import java.lang.reflect.Method;

import net.madz.lifecycle.meta.MetaObject;
import net.madz.lifecycle.meta.template.TransitionMetadata;
import net.madz.meta.FlavorMetaData;

public interface TransitionObject extends MetaObject<TransitionObject, TransitionMetadata>, FlavorMetaData<StateMachineObject> {

    Method getTransitionMethod();
}
