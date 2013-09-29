package net.madz.lifecycle.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import net.madz.lifecycle.annotations.typed.ITypedReactiveObject;
import net.madz.lifecycle.annotations.typed.ITypedState;
import net.madz.lifecycle.annotations.typed.ITypedTransition;
import net.madz.lifecycle.annotations.typed.TypedStateContext;
import net.madz.lifecycle.annotations.typed.TypedStateMachine;
import net.madz.lifecycle.annotations.typed.TypedTransition;
import net.madz.lifecycle.meta.StateMachineMetaData;
import net.madz.lifecycle.meta.StateMetaData;
import net.madz.lifecycle.meta.TransitionMetaData;
import net.madz.lifecycle.meta.impl.StateMachineMetaDataBuilderImpl;
import net.madz.util.StringUtil;

import org.apache.log4j.Logger;

public class TransitionInvocationHandler<R extends ITypedReactiveObject, S extends ITypedState<R, S>, T extends ITypedTransition> implements InvocationHandler {

    private final R reactiveObject;

    public TransitionInvocationHandler(R reactiveObject) {
        super();
        this.reactiveObject = reactiveObject;
    }

    @Override
    public Object invoke(final Object object, final Method method, final Object[] args) throws Throwable {
        StateMachineMetaData<R, S, T> stateMachineMetaData = findStateMachineMetaData();
        synchronized (reactiveObject) {
            S state = reactiveObject.getState();
            StateMetaData<R, S> stateMetaData = stateMachineMetaData.getStateMetaData(state);
            final String transitionName;
            final TypedTransition transition = method.getAnnotation(TypedTransition.class);
            if ( null == transition ) {
                return method.invoke(reactiveObject, args);
            }
            if ( TypedTransition.NULL.equals(transition.value()) ) {
                transitionName = StringUtil.toUppercaseFirstCharacter(method.getName());
            } else {
                transitionName = transition.value();
            }
            final T transitionEnum = stateMachineMetaData.getTransition(transitionName);
            final TransitionMetaData transitionMetaData = stateMachineMetaData.getTransitionMetaData(transitionEnum);
            if ( stateMetaData.illegalTransition(transitionEnum) ) {
                throw new IllegalStateException("Cannot transit from State:" + stateMetaData.getDottedPath().getName() + " via Transition: " + transitionName);
            }
            final S nextState = stateMetaData.nextState(transitionEnum);
            final TypedStateContext<R, S> context = new TypedStateContext<R, S>(reactiveObject, nextState, transitionEnum, args);
            intercept(context);
            final FutureTask<Object> task = new FutureTask<Object>(new Callable<Object>() {

                @Override
                public Object call() throws Exception {
                    synchronized (reactiveObject) {
                        final Object result = method.invoke(reactiveObject, args);
                        reactiveObject.notify();
                        return result;
                    }
                }
            });
            final Thread t = new Thread(task);
            t.start();
            reactiveObject.wait(transitionMetaData.getTimeout() / 2);
            try {
                final Object result = task.get(transitionMetaData.getTimeout() / 2, TimeUnit.MILLISECONDS);
                if ( context.getCurrentState() != nextState ) {
                    final Method stateSetter = reactiveObject.getClass().getDeclaredMethod("setState", new Class[] { nextState.getClass() });
                    stateSetter.setAccessible(true);
                    stateSetter.invoke(reactiveObject, nextState);
                    stateSetter.setAccessible(false);
                }
                notify(context);
                return result;
            } catch (Exception ex) {
                Logger.getLogger(getClass()).error("Failed to process transition: " + transition, ex);
                throw ex;
            }
        }
    }

    private void intercept(TypedStateContext<R, S> context) throws Throwable {
        InterceptorHub.INSTANCE.intercept(context);
    }

    private void notify(final TypedStateContext<R, S> context) {
        try {
            StateChangeListenerHub.INSTANCE.notify(context);
        } catch (Throwable ignored) {
        }
    }

    @SuppressWarnings("unchecked")
    private StateMachineMetaData<R, S, T> findStateMachineMetaData() {
        final Class<? extends ITypedReactiveObject> reactiveClass = reactiveObject.getClass();
        Class<? extends ITypedReactiveObject> stateMachineClass = null;
        for ( Class<?> interfaze : reactiveClass.getInterfaces() ) {
            TypedStateMachine annotation = (TypedStateMachine) interfaze.getAnnotation(TypedStateMachine.class);
            if ( null == annotation ) {
                continue;
            } else {
                stateMachineClass = (Class<? extends ITypedReactiveObject>) interfaze;
                break;
            }
        }
        if ( null == stateMachineClass ) {
            throw new IllegalStateException("Cannot find stateMachineClass through interfaces of Class: " + reactiveClass.getName());
        }
        final StateMachineMetaDataBuilderImpl builder = new StateMachineMetaDataBuilderImpl(null, "StateMachine");
        final StateMachineMetaData<R, S, T> machineMetaData = (StateMachineMetaData<R, S, T>) builder.build(null, stateMachineClass);
        return machineMetaData;
    }
}
