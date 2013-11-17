package net.madz.bcel.intercept;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.LifecycleCommonErrors;
import net.madz.lifecycle.LifecycleEventHandler;
import net.madz.lifecycle.LifecycleException;
import net.madz.lifecycle.LifecycleLockStrategry;
import net.madz.lifecycle.annotations.relation.Relation;
import net.madz.lifecycle.impl.LifecycleEventImpl;
import net.madz.lifecycle.meta.instance.StateMachineObject;
import net.madz.lifecycle.meta.template.StateMetadata;

public class LifecycleInterceptor<V> extends Interceptor<V> {

    private static final Logger logger = Logger.getLogger(LifecycleInterceptor.class.getName());
    private static volatile AbsStateMachineRegistry registry = null;
    static {
        synchronized (AbsStateMachineRegistry.class) {
            final String registryClass = System.getProperty("net.madz.lifecycle.StateMachineRegistry");
            if ( null != registryClass ) {
                try {
                    // TODO There are two patterns to use registry:
                    // 1. using Annotation to register LifecycleMetadata
                    // classes.
                    // 2. using
                    // AbsStateMachineRegistry.registerLifecycleMeta(Class<?>
                    // class);
                    // For now ONLY 1st pattern is supported with extending
                    // AbsStateMachineRegistry.
                    registry = (AbsStateMachineRegistry) Class.forName(registryClass).newInstance();
                } catch (Throwable t) {
                    logger.log(Level.SEVERE, "Cannot Instantiate State Machine Registry: " + registryClass, t);
                    throw new IllegalStateException(t);
                }
            } else {
                registry = DefaultStateMachineRegistry.getInstance();
            }
        }
    }

    private static synchronized StateMachineObject lookupStateMachine(InterceptContext<?> context) {
        return registry.getStateMachineInst(context.getTarget().getClass());
    }

    public LifecycleInterceptor(Interceptor<V> next) {
        super(next);
        System.out.println("Intercepting....instantiating LifecycleInterceptor");
    }

    @Override
    protected void cleanup(InterceptContext<V> context) {
        super.cleanup(context);
        System.out.println("Intercepting....LifecycleInterceptor is doing cleanup ...");
    }

    @Override
    protected void postExec(InterceptContext<V> context) {
        super.postExec(context);
        final StateMachineObject stateMachine = lookupStateMachine(context);
        try {
            if ( !nextStateCanBeEvaluatedBeforeTranstion(stateMachine, context) ) {
                validateNextStateInboundWhile(stateMachine, context);
            }
            setNextState(stateMachine, context);
            performCallbacksAfterStateChange(stateMachine, context);
        } finally {
            if ( isLockEnabled(stateMachine) ) {
                unlock(stateMachine, context);
            }
            fireLifecycleEvents(stateMachine, context);
        }
        System.out.println("Intercepting....LifecycleInterceptor is doing postExec ...");
    }

    private void fireLifecycleEvents(StateMachineObject stateMachine, InterceptContext<V> context) {
        final LifecycleEventHandler eventHandler = registry.getLifecycleEventHandler();
        if ( null != eventHandler ) {
            eventHandler.onEvent(new LifecycleEventImpl(context));
        }
    }

    private void unlock(StateMachineObject stateMachine, InterceptContext<V> context) {
        final LifecycleLockStrategry lockStrategry = context.getLockStrategry();
        if ( null == lockStrategry ) {
            return;
        }
        final Object targetReactiveObject = context.getTarget();
        final Object parentReactiveObject = stateMachine.evaluateParent(context.getTarget());
        final Object[] relativeObjects = stateMachine.evaluateRelatives(context.getTarget());
        for ( Object relative : relativeObjects ) {
            lockStrategry.unlockRelative(relative);
        }
        if ( null != parentReactiveObject ) {
            lockStrategry.unlockParent(parentReactiveObject);
        }
        lockStrategry.unlock(targetReactiveObject);
    }

    private boolean isLockEnabled(StateMachineObject stateMachine) {
        return null != stateMachine.getLifecycleLockStrategy();
    }

    private void performCallbacksAfterStateChange(StateMachineObject stateMachine, InterceptContext<V> context) {
        // TODO Auto-generated method stub
    }

    private void setNextState(StateMachineObject stateMachine, InterceptContext<V> context) {
        final String stateName = stateMachine.getNextState(context.getTarget(), context.getTranstionKey());
        stateMachine.setTargetState(context.getTarget(), stateName);
    }

    private void validateNextStateInboundWhile(StateMachineObject stateMachine, InterceptContext<V> context) {
        stateMachine.validateInboundWhiles(context);
    }

    private boolean nextStateCanBeEvaluatedBeforeTranstion(StateMachineObject stateMachine, InterceptContext<V> context) {
        if ( hasOnlyOneStateCandidate(stateMachine, context) ) {
            return true;
        } else if ( canEvaluateConditionBeforeTransition(stateMachine, context) ) {
            return true;
        }
        return false;
    }

    private boolean canEvaluateConditionBeforeTransition(StateMachineObject stateMachine, InterceptContext<V> context) {
        return stateMachine.evaluateConditionBeforeTransition(context.getTranstionKey());
    }

    private boolean hasOnlyOneStateCandidate(StateMachineObject stateMachine, InterceptContext<V> context) {
        final String stateName = stateMachine.evaluateState(context.getTarget());
        final StateMetadata state = stateMachine.getMetaType().getState(stateName);
        if ( state.hasMultipleStateCandidatesOn(context.getTranstionKey()) ) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void preExec(InterceptContext<V> context) {
        super.preExec(context);
        final StateMachineObject stateMachine = lookupStateMachine(context);
        if ( isLockEnabled(stateMachine) ) {
            final LifecycleLockStrategry lock = stateMachine.getLifecycleLockStrategy();
            context.setLockStrategry(lock);
            lock(stateMachine, context);
        }
        validateStateValidWhiles(stateMachine, context);
        validateTransition(stateMachine, context);
        if ( nextStateCanBeEvaluatedBeforeTranstion(stateMachine, context) ) {
            validateNextStateInboundWhile(stateMachine, context);
        }
        performCallbacksBeforeStateChange(stateMachine, context);
    }

    private void performCallbacksBeforeStateChange(StateMachineObject stateMachine, InterceptContext<V> context) {
        // TODO Auto-generated method stub
    }

    private void validateTransition(StateMachineObject stateMachine, InterceptContext<V> context) {
        String stateName = stateMachine.evaluateState(context.getTarget());
        StateMetadata stateMetadata = stateMachine.getMetaType().getState(stateName);
        if ( null == stateMetadata.getTransition(context.getTranstionKey()) ) {
            throw new LifecycleException(getClass(), "lifecycle_common", LifecycleCommonErrors.ILLEGAL_TRANSITION_ON_STATE, context.getTranstionKey(),
                    stateName, context.getTarget());
        }
    }

    private void validateStateValidWhiles(StateMachineObject stateMachine, InterceptContext<V> context) {
        stateMachine.validateValidWhiles(context);
    }

    private void lock(StateMachineObject stateMachine, InterceptContext<V> context) {
        final LifecycleLockStrategry lockStrategry = context.getLockStrategry();
        if ( null == lockStrategry ) {
            return;
        }
        final Object targetReactiveObject = context.getTarget();
        final Object parentReactiveObject = stateMachine.evaluateParent(context.getTarget());
        final Object[] relativeObjects = stateMachine.evaluateRelatives(context.getTarget());
        lockStrategry.lock(targetReactiveObject);
        if ( null != parentReactiveObject ) {
            lockStrategry.lockParent(parentReactiveObject);
        }
        for ( Object relative : relativeObjects ) {
            lockStrategry.lockRelative(relative);
        }
    }

    // processRelations(context);
    // System.out.println("Intercepting....LifecycleInterceptor is doing preExec ...");
    // Annotation[] annotation = context.getAnnotation();
    // System.out.println("Found Annotations: ");
    // for ( Annotation annotation2 : annotation ) {
    // System.out.println(annotation2);
    // }
    private void processRelations(InterceptContext<V> context) {
        final Object[] arguments = context.getArguments();
        final Method method = context.getMethod();
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        final HashMap<Class<?>, Integer> relationIndexMap = new HashMap<>();
        int parameterIndex = 0;
        for ( Annotation[] annotations : parameterAnnotations ) {
            for ( Annotation annotation : annotations ) {
                if ( Relation.class == annotation.annotationType() ) {
                    relationIndexMap.put(( (Relation) annotation ).value(), parameterIndex);
                }
            }
            parameterIndex++;
        }
        if ( relationIndexMap.size() > 0 ) {
            for ( Entry<Class<?>, Integer> entry : relationIndexMap.entrySet() ) {
                System.out.println("Relation Key Class: " + entry.getKey());
                System.out.println("Index @Arguments: " + entry.getValue());
                System.out.println(String.valueOf(arguments[entry.getValue()]));
            }
        }
    }
}