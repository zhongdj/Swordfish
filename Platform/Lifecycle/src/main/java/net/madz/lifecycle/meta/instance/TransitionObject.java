package net.madz.lifecycle.meta.instance;

import java.lang.reflect.Method;

import net.madz.lifecycle.meta.Concrete;
import net.madz.lifecycle.meta.template.TransitionMetadata;
import net.madz.meta.FlavorMetaData;

public interface TransitionObject extends Concrete<TransitionMetadata> , FlavorMetaData<StateMachineObject>{

    Method getTransitionMethod();
}
