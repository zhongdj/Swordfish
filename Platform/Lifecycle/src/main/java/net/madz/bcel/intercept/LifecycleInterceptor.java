package net.madz.bcel.intercept;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.LifecycleCommonErrors;
import net.madz.lifecycle.LifecycleEventHandler;
import net.madz.lifecycle.LifecycleException;
import net.madz.lifecycle.LifecycleLockStrategry;
import net.madz.lifecycle.impl.LifecycleEventImpl;
import net.madz.lifecycle.meta.instance.StateMachineObject;
import net.madz.lifecycle.meta.template.StateMetadata;
import net.madz.lifecycle.meta.template.TransitionMetadata;

public class LifecycleInterceptor<V> extends Interceptor<V> {

    private static final Logger logger = Logger.getLogger("Lifecycle Framework");
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
        if ( logger.isLoggable(Level.FINE) ) {
            logger.fine("Intercepting....instantiating LifecycleInterceptor");
        }
    }

    @Override
    protected void handleException(InterceptContext<V> context, Throwable e) {
        context.setFailureCause(e);
        super.handleException(context, e);
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
        // Set From State Before all instructions.
        context.setFromState(stateMachine.evaluateState(context.getTarget()));
        if ( logger.isLoggable(Level.FINE) ) {
            logger.fine("intercepting  [" + context.getTarget() + "]" + "\n\tfrom state: [" + context.getFromState() + "] ");
        }
        // 1. Validate State validity
        if ( logger.isLoggable(Level.FINE) ) {
            logger.fine("\tStep 1. start validating State [" + context.getFromState() + "]");
        }
        validateStateValidWhiles(stateMachine, context);
        // 2. Validate Transition validity
        if ( logger.isLoggable(Level.FINE) ) {
            logger.fine("\tStep 2. start validating transition: [" + context.getTransitionKey() + "] on state: [" + context.getFromState() + "]");
        }
        validateTransition(stateMachine, context);
        // 3. Validate in-bound Relation constraint if next state is predictable
        // before method invocation
        if ( logger.isLoggable(Level.FINE) ) {
            logger.fine("\tStep 3. start validating inbound relation constraint is next state is predictable before method invocation.");
        }
        if ( nextStateCanBeEvaluatedBeforeTranstion(stateMachine, context) ) {
            validateNextStateInboundWhile(stateMachine, context);
        }
        // 4. Callback before state change
        if ( logger.isLoggable(Level.FINE) ) {
            logger.fine("\tStep 4. start callback before state change.");
        }
        performCallbacksBeforeStateChange(stateMachine, context);
    }

    @Override
    protected void postExec(InterceptContext<V> context) {
        super.postExec(context);
        final StateMachineObject stateMachine = lookupStateMachine(context);
        try {
            // 5. Validate in-bound Relation constraint if next state is
            // predictable after method invocation.
            if ( logger.isLoggable(Level.FINE) ) {
                logger.fine("\tStep 5. start validating inbound relation constraint is next state after method invocation.");
            }
            if ( !nextStateCanBeEvaluatedBeforeTranstion(stateMachine, context) ) {
                validateNextStateInboundWhile(stateMachine, context);
            }
            // 6. Setup next state
            if ( logger.isLoggable(Level.FINE) ) {
                logger.fine("\tStep 6. Set next state to reactiveObject.");
            }
            setNextState(stateMachine, context);
            if ( logger.isLoggable(Level.FINE) ) {
                logger.fine("\tStep 6. ReactiveObject is tranisited to state: [" + context.getToState() + "]");
            }
            // 7. Callback after state change
            if ( logger.isLoggable(Level.FINE) ) {
                logger.fine("\tStep 7. Start Callback after state change.");
            }
            performCallbacksAfterStateChange(stateMachine, context);
            context.setSuccess(true);
        } finally {
            if ( isLockEnabled(stateMachine) ) {
                unlock(stateMachine, context);
            }
            context.end();
            // 8. Fire state change notification events.
            if ( logger.isLoggable(Level.FINE) ) {
                logger.fine("\tStep 8. Start fire state change event.");
            }
            fireLifecycleEvents(stateMachine, context);
        }
    }

    @Override
    protected void cleanup(InterceptContext<V> context) {
        super.cleanup(context);
        if ( logger.isLoggable(Level.FINE) ) {
            logger.fine("Intercepting....LifecycleInterceptor is doing cleanup ...");
        }
        final StateMachineObject stateMachine = lookupStateMachine(context);
        if ( !context.isSuccess() && isLockEnabled(stateMachine) ) {
            unlock(stateMachine, context);
        }
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
            final LifecycleLockStrategry relativeLock = stateMachine.getRelatedStateMachine(relative.getClass()).getLifecycleLockStrategy();
            relativeLock.unlockRead(relative);
        }
        if ( null != parentReactiveObject ) {
            final LifecycleLockStrategry parentLock = stateMachine.getParentStateMachine().getLifecycleLockStrategy();
            parentLock.unlockRead(parentReactiveObject);
        }
        lockStrategry.unlockWrite(targetReactiveObject);
    }

    private boolean isLockEnabled(StateMachineObject stateMachine) {
        return null != stateMachine.getLifecycleLockStrategy();
    }

    private void performCallbacksAfterStateChange(StateMachineObject stateMachine, InterceptContext<V> context) {
        
    }

    private void setNextState(StateMachineObject stateMachine, InterceptContext<V> context) {
        final String stateName = stateMachine.getNextState(context.getTarget(), context.getTransitionKey());
        stateMachine.setTargetState(context.getTarget(), stateName);
        context.setToState(stateName);
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
        return stateMachine.evaluateConditionBeforeTransition(context.getTransitionKey());
    }

    private boolean hasOnlyOneStateCandidate(StateMachineObject stateMachine, InterceptContext<V> context) {
        final String stateName = stateMachine.evaluateState(context.getTarget());
        final StateMetadata state = stateMachine.getMetaType().getState(stateName);
        if ( state.hasMultipleStateCandidatesOn(context.getTransitionKey()) ) {
            return false;
        } else {
            return true;
        }
    }

    private void performCallbacksBeforeStateChange(StateMachineObject stateMachine, InterceptContext<V> context) {
        // TODO Auto-generated method stub
    }

    private void validateTransition(StateMachineObject stateMachine, InterceptContext<V> context) {
        StateMetadata stateMetadata = stateMachine.getMetaType().getState(context.getFromState());
        TransitionMetadata transition = stateMetadata.getTransition(context.getTransitionKey());
        if ( null == transition ) {
            throw new LifecycleException(getClass(), "lifecycle_common", LifecycleCommonErrors.ILLEGAL_TRANSITION_ON_STATE, context.getTransitionKey(),
                    context.getFromState(), context.getTarget());
        } else {
            context.setTransitionType(transition.getType());
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
        lockStrategry.lockWrite(targetReactiveObject);
        if ( null != parentReactiveObject ) {
            final LifecycleLockStrategry parentLock = stateMachine.getParentStateMachine().getLifecycleLockStrategy();
            if ( null != parentLock ) {
                parentLock.lockRead(parentReactiveObject);
            }
        }
        for ( Object relative : relativeObjects ) {
            final LifecycleLockStrategry relativeLock = stateMachine.getRelatedStateMachine(relative.getClass()).getLifecycleLockStrategy();
            relativeLock.lockRead(relative);
        }
    }
}