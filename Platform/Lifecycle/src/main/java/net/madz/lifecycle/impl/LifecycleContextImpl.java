package net.madz.lifecycle.impl;

import java.lang.reflect.Method;
import java.util.Arrays;

import net.madz.bcel.intercept.InterceptContext;
import net.madz.lifecycle.LifecycleContext;
import net.madz.lifecycle.StateConverter;

public class LifecycleContextImpl<T, S> implements LifecycleContext<T, S> {

    private final T target;
    private final S fromState;
    private final S toState;
    private final Method transitionMethod;
    private final Object[] arguments;

    @SuppressWarnings("unchecked")
    public LifecycleContextImpl(InterceptContext<T> context, StateConverter<S> converter) {
        this.target = context.getTarget();
        if ( null != converter ) {
            this.fromState = converter.fromState(context.getFromState());
        } else {
            this.fromState = (S) context.getFromState();
        }
        if ( null == context.getToState() ) {
            this.toState = null;
        } else {
            if ( null != converter ) {
                this.toState = converter.fromState(context.getToState());
            } else {
                this.toState = (S) context.getToState();
            }
        }
        this.transitionMethod = context.getMethod();
        this.arguments = context.getArguments();
    }

    @Override
    public T getTarget() {
        return target;
    }

    @Override
    public S getFromState() {
        return fromState;
    }

    @Override
    public S getToState() {
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
