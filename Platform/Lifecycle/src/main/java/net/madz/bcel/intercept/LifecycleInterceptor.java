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
import net.madz.verification.VerificationException;

public class LifecycleInterceptor<V> extends Interceptor<V> {

    private static final Logger logger = Logger.getLogger("Lifecycle Framework");

    private static synchronized StateMachineObject lookupStateMachine(InterceptContext<?> context) {
        try {
            return AbsStateMachineRegistry.getInstance().loadStateMachineObject(context.getTarget().getClass());
        } catch (VerificationException e) {
            throw new IllegalStateException("Should not encounter syntax verification exception at intercepting runtime", e);
        }
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
            lock.lockWrite(context.getTarget());
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
            unlockRelationObjects(context);
            if ( isLockEnabled(stateMachine) ) {
                final LifecycleLockStrategry lock = stateMachine.getLifecycleLockStrategy();
                if ( null != lock ) {
                    lock.unlockWrite(context.getTarget());
                }
            }
            context.end();
            // 8. Fire state change notification events.
            if ( logger.isLoggable(Level.FINE) ) {
                logger.fine("\tStep 8. Start fire state change event.");
            }
            fireLifecycleEvents(stateMachine, context);
        }
    }

    private void unlockRelationObjects(UnlockableStack stack) {
        Unlockable unlockable = null;
        while ( !stack.isEmpty() ) {
            unlockable = stack.popUnlockable();
            unlockable.unlock();
        }
    }

    @Override
    protected void cleanup(InterceptContext<V> context) {
        super.cleanup(context);
        if ( logger.isLoggable(Level.FINE) ) {
            logger.fine("Intercepting....LifecycleInterceptor is doing cleanup ...");
        }
        unlockRelationObjects(context);
        final StateMachineObject stateMachine = lookupStateMachine(context);
        if ( !context.isSuccess() && isLockEnabled(stateMachine) ) {
            final LifecycleLockStrategry lockStrategry = stateMachine.getLifecycleLockStrategy();
            if ( null != lockStrategry ) {
                lockStrategry.unlockWrite(context.getTarget());
            }
        }
    }

    private void fireLifecycleEvents(StateMachineObject stateMachine, InterceptContext<V> context) {
        final LifecycleEventHandler eventHandler = AbsStateMachineRegistry.getInstance().getLifecycleEventHandler();
        if ( null != eventHandler ) {
            eventHandler.onEvent(new LifecycleEventImpl(context));
        }
    }

    private boolean isLockEnabled(StateMachineObject stateMachine) {
        return null != stateMachine.getLifecycleLockStrategy();
    }

    private void performCallbacksAfterStateChange(StateMachineObject stateMachine, InterceptContext<V> context) {}

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
        if ( !stateMetadata.isTransitionValid(context.getTransitionKey()) ) {
            throw new LifecycleException(getClass(), "lifecycle_common", LifecycleCommonErrors.ILLEGAL_TRANSITION_ON_STATE, context.getTransitionKey(),
                    context.getFromState(), context.getTarget());
        } else {
            TransitionMetadata transition = stateMetadata.getTransition(context.getTransitionKey());
            context.setTransitionType(transition.getType());
            context.setTransition(transition.getDottedPath().getName());
        }
    }

    private void validateStateValidWhiles(StateMachineObject stateMachine, InterceptContext<V> context) {
        stateMachine.validateValidWhiles(context);
    }
}