package net.madz.lifecycle.meta.instance;

import java.lang.reflect.Method;

import net.madz.lifecycle.meta.Instance;
import net.madz.lifecycle.meta.template.TransitionMetadata;
import net.madz.meta.FlavorMetaData;

public interface TransitionInst extends Instance<TransitionMetadata> , FlavorMetaData<StateMachineInst>{

    Method getTransitionMethod();
}
