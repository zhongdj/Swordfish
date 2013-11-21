package net.madz.lifecycle.impl;

import java.lang.reflect.Method;
import java.util.Arrays;

import net.madz.bcel.intercept.InterceptContext;
import net.madz.lifecycle.LifecycleContext;

public class LifecycleContextImpl<T> implements LifecycleContext<T, String> {

    private final T target;
    private final String fromState;
    private final String toState;
    private final Method transitionMethod;
    private final Object[] arguments;

    public LifecycleContextImpl(InterceptContext<T> context) {
        this.target = context.getTarget();
        this.fromState = context.getFromState();
        this.toState = context.getToState();
        this.transitionMethod = context.getMethod();
        this.arguments = context.getArguments();
    }

    @Override
    public T getTarget() {
        return target;
    }

    @Override
    public String getFromState() {
        return fromState;
    }

    @Override
    public String getToState() {
        return toState;
    }

    @Override
    public Method getTransitionMethod() {
        return transitionMethod;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return "LifecycleContextImpl [target=" + target + ", fromState=" + fromState + ", toState=" + toState + ", transitionMethod=" + transitionMethod
                + ", arguments=" + Arrays.toString(arguments) + "]";
    }
}
