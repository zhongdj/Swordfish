package net.madz.lifecycle;

import java.lang.reflect.Method;

public interface LifecycleContext<T, S> {

    T getTarget();

    S getFromState();

    S getToState();

    Method getTransitionMethod();

    Object[] getArguments();
}
