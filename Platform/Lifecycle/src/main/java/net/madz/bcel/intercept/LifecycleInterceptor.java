package net.madz.bcel.intercept;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.madz.bcel.intercept.helper.InterceptorHelper;
import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.LifecycleEventHandler;
import net.madz.lifecycle.LifecycleLockStrategry;
import net.madz.lifecycle.impl.LifecycleEventImpl;
import net.madz.lifecycle.meta.instance.StateMachineObject;
import net.madz.lifecycle.meta.template.StateMetadata;

public class LifecycleInterceptor<V, R> extends Interceptor<V, R> {

    private static final Logger logger = Logger.getLogger("Lifecycle Framework");

    public LifecycleInterceptor(Interceptor<V, R> next) {
        super(next);
        if ( logger.isLoggable(Level.FINE) ) {
            logger.fine("Intercepting....instantiating LifecycleInterceptor");
        }
    }

    private boolean canEvaluateConditionBeforeTransition(StateMachineObject<?> stateMachine, InterceptContext<V, R> context) {
        return stateMachine.evaluateConditionBeforeTransition(context.getTransitionKey());
    }

    @Override
    protected void cleanup(InterceptContext<V, R> context) {
        super.cleanup(context);
        logCleanup(context);
        unlockRelationObjects(context);
        final StateMachineObject<?> stateMachine = InterceptorHelper.lookupStateMachine(context);
        if ( !context.isSuccess() && isLockEnabled(stateMachine) ) {
            final LifecycleLockStrategry lockStrategry = stateMachine.getLifecycleLockStrategy();
            if ( null != lockStrategry ) {
                lockStrategry.unlockWrite(context.getTarget());
            }
        }
    }

    private void doLock(InterceptContext<V, R> context, final StateMachineObject<?> stateMachine) {
        if ( isLockEnabled(stateMachine) ) {
            final LifecycleLockStrategry lock = stateMachine.getLifecycleLockStrategy();
            lock.lockWrite(context.getTarget());
        }
    }

    private void doUnlock(InterceptContext<V, R> context, final StateMachineObject<?> stateMachine) {
        unlockRelationObjects(context);
        if ( isLockEnabled(stateMachine) ) {
            final LifecycleLockStrategry lock = stateMachine.getLifecycleLockStrategy();
            if ( null != lock ) {
                lock.unlockWrite(context.getTarget());
            }
        }
    }

    private void fireLifecycleEvents(StateMachineObject<?> stateMachine, InterceptContext<V, R> context) {
        context.logStep8FireLifecycleEvents();
        final LifecycleEventHandler eventHandler = AbsStateMachineRegistry.getInstance().getLifecycleEventHandler();
        if ( null != eventHandler ) {
            eventHandler.onEvent(new LifecycleEventImpl(context));
        }
    }

    @Override
    protected void handleException(InterceptContext<V, R> context, Throwable e) {
        context.setFailureCause(e);
        super.handleException(context, e);
    }

    private boolean hasOnlyOneStateCandidate(StateMachineObject<?> stateMachine, InterceptContext<V, R> context) {
        final String stateName = stateMachine.evaluateState(context.getTarget());
        final StateMetadata state = stateMachine.getMetaType().getState(stateName);
        if ( state.hasMultipleStateCandidatesOn(context.getTransitionKey()) ) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isLockEnabled(StateMachineObject<?> stateMachine) {
        return null != stateMachine.getLifecycleLockStrategy();
    }

    private void logCleanup(InterceptContext<V, R> context) {
        if ( logger.isLoggable(Level.FINE) ) {
            logger.fine("Intercepting....LifecycleInterceptor is doing cleanup ...");
            context.logResultFromContext();
        }
    }

    private boolean nextStateCanBeEvaluatedBeforeTranstion(StateMachineObject<?> stateMachine, InterceptContext<V, R> context) {
        if ( hasOnlyOneStateCandidate(stateMachine, context) ) {
            return true;
        } else if ( canEvaluateConditionBeforeTransition(stateMachine, context) ) {
            return true;
        }
        return false;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void performCallbacksAfterStateChange(StateMachineObject stateMachine, InterceptContext<V, R> context) {
        context.logStep7Callback();
        stateMachine.performPostStateChangeCallback(context);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void performCallbacksBeforeStateChange(StateMachineObject stateMachine, InterceptContext<V, R> context) {
        context.logStep4PreStateChangeCallback();
        stateMachine.performPreStateChangeCallback(context);
    }

    @Override
    protected void postExec(InterceptContext<V, R> context) {
        super.postExec(context);
        final StateMachineObject<?> stateMachine = InterceptorHelper.lookupStateMachine(context);
        try {
            validateInboundConstraintAfterMethodInvocation(context, stateMachine);
            context.setupNextState(stateMachine);
            performCallbacksAfterStateChange(stateMachine, context);
            context.setSuccess(true);
        } finally {
            doUnlock(context, stateMachine);
            context.end();
            fireLifecycleEvents(stateMachine, context);
        }
    }

    @Override
    protected void preExec(InterceptContext<V, R> context) {
        super.preExec(context);
        final StateMachineObject<?> stateMachine = InterceptorHelper.lookupStateMachine(context);
        doLock(context, stateMachine);
        context.setFromState(stateMachine.evaluateState(context.getTarget()));
        if ( logger.isLoggable(Level.FINE) ) {
            logger.fine("intercepting  [" + context.getTarget() + "]" + "\n\tfrom state: [" + context.getFromState() + "] ");
        }
        validateStateValidWhiles(stateMachine, context);
        context.validateTransition(stateMachine);
        validateInboundConstrantBeforeMethodInvocation(context, stateMachine);
        performCallbacksBeforeStateChange(stateMachine, context);
    }

    private void unlockRelationObjects(UnlockableStack stack) {
        Unlockable unlockable = null;
        while ( !stack.isEmpty() ) {
            unlockable = stack.popUnlockable();
            unlockable.unlock();
        }
    }

    private void validateInboundConstraintAfterMethodInvocation(InterceptContext<V, R> context, final StateMachineObject<?> stateMachine) {
        context.logStep5ValiatingInbound();
        if ( !nextStateCanBeEvaluatedBeforeTranstion(stateMachine, context) ) {
            validateNextStateInboundWhile(stateMachine, context);
        }
    }

    private void validateInboundConstrantBeforeMethodInvocation(InterceptContext<V, R> context, final StateMachineObject<?> stateMachine) {
        context.logStep3ValidateInboundConstrantBeforeMethodInvocation();
        if ( nextStateCanBeEvaluatedBeforeTranstion(stateMachine, context) ) {
            validateNextStateInboundWhile(stateMachine, context);
        }
    }

    private void validateNextStateInboundWhile(StateMachineObject<?> stateMachine, InterceptContext<V, R> context) {
        stateMachine.validateInboundWhiles(context);
    }

    private void validateStateValidWhiles(StateMachineObject<?> stateMachine, InterceptContext<V, R> context) {
        context.logStep1ValidateCurrentState();
        stateMachine.validateValidWhiles(context);
    }
}