package net.madz.lifecycle.meta.instance;

import java.lang.reflect.Method;

import net.madz.lifecycle.meta.Instance;
import net.madz.lifecycle.meta.template.TransitionMetadata;

public interface TransitionInst extends Instance<TransitionMetadata> {

    Method getTransitionMethod();
}
