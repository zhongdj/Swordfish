package net.madz.bcel.intercept;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.madz.lifecycle.annotations.Null;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.meta.instance.StateMachineObject;
import net.madz.lifecycle.meta.template.TransitionMetadata;
import net.madz.lifecycle.meta.template.TransitionMetadata.TransitionTypeEnum;
import net.madz.util.StringUtil;

public class InterceptContext<V, R> implements UnlockableStack {

    private static final Logger logger = Logger.getLogger("Lifecycle Framework");
    private final Stack<Unlockable> lockedRelatedObjectStack = new Stack<>();
    private final long startTime;
    private final Annotation[] annotation;
    private final Class<?> klass;
    private final Method method;
    private final V target;
    private final Object[] arguments;
    private String fromState;
    private String nextState;
    private String transition;
    private TransitionTypeEnum transitionType;
    private long endTime;
    private boolean success;
    private Throwable failureCause;

    public InterceptContext(final Class<?> klass, final V target, final String methodName, final Class<?>[] argsType, final Object[] arguments) {
        this.klass = klass;
        this.method = findMethod(klass, methodName, argsType);
        this.annotation = method.getAnnotations();
        this.target = target;
        this.startTime = System.currentTimeMillis();
        if ( null == arguments ) {
            this.arguments = new Object[0];
        } else {
            this.arguments = arguments;
        }
        logInterceptPoint(klass, methodName);
    }

    public Interceptor<V, R> createInterceptorChain() {
        return new LifecycleInterceptor<V, R>(new CallableInterceptor<V, R>());
    }

    public void end() {
        this.endTime = System.currentTimeMillis();
    }

    protected Method findMethod(Class<?> klass, String methodName, Class<?>[] classes) {
        try {
            return klass.getDeclaredMethod(methodName, classes);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public Annotation[] getAnnotation() {
        return annotation;
    }

    public Object[] getArguments() {
        return this.arguments;
    }

    public long getEndTime() {
        return endTime;
    }

    public Throwable getFailureCause() {
        return failureCause;
    }

    public String getFromState() {
        return fromState;
    }

    public Class<?> getKlass() {
        return klass;
    }

    public Method getMethod() {
        return method;
    }

    public long getStartTime() {
        return startTime;
    }

    public V getTarget() {
        return target;
    }

    public String getToState() {
        return nextState;
    }

    public String getTransition() {
        return transition;
    }

    public Object getTransitionKey() {
        final Class<?> keyClass = method.getAnnotation(Transition.class).value();
        if ( Null.class.equals(keyClass) ) {
            return StringUtil.toUppercaseFirstCharacter(method.getName());
        } else {
            return keyClass;
        }
    }

    public TransitionTypeEnum getTransitionType() {
        return transitionType;
    }

    @Override
    public boolean isEmpty() {
        return this.lockedRelatedObjectStack.isEmpty();
    }

    public boolean isSuccess() {
        return success;
    }

    private void logInterceptPoint(final Class<?> klass, final String methodName) {
        if ( logger.isLoggable(Level.FINE) ) {
            final StringBuilder sb = new StringBuilder(" ");
            for ( Object o : this.arguments ) {
                sb.append(String.valueOf(o)).append(" ");
            }
            logger.fine("Found Intercept Point: " + klass + "." + methodName + "( " + sb.toString() + " )");
            logger.fine("Intercepting....instatiating InterceptContext ...");
        }
    }

    public void logResultFromContext() {
        if ( !this.isSuccess() ) {
            String toStateString = null == this.getToState() ? "(Had Not Been Evaluated)" : this.getToState();
            logger.severe("ReactiveObject: [" + this.getTarget() + "] was failed to transit from state: [" + this.getFromState() + "] to state: ["
                    + toStateString + "] with following error: ");
            logger.severe(this.getFailureCause().getLocalizedMessage());
        }
    }

    public void logStep1ValidateCurrentState() {
        if ( logger.isLoggable(Level.FINE) ) {
            logger.fine("\tStep 1. start validating State [" + this.getFromState() + "]");
        }
    }

    public void logStep2validateTransition() {
        if ( logger.isLoggable(Level.FINE) ) {
            logger.fine("\tStep 2. start validating transition: [" + this.getTransitionKey() + "] on state: [" + this.getFromState() + "]");
        }
    }

    public void logStep3ValidateInboundConstrantBeforeMethodInvocation() {
        if ( logger.isLoggable(Level.FINE) ) {
            logger.fine("\tStep 3. start validating inbound relation constraint is next state is predictable before method invocation.");
        }
    }

    public void logStep4PreStateChangeCallback() {
        if ( logger.isLoggable(Level.FINE) ) {
            logger.fine("\tStep 4. start callback before state change from : " + this.getFromState() + " => to : " + this.getToState());
        }
    }

    public void logStep5ValiatingInbound() {
        if ( logger.isLoggable(Level.FINE) ) {
            logger.fine("\tStep 5. start validating inbound relation constraint is next state after method invocation.");
        }
    }

    public void logStep6_2SetupNextStateFinsihed() {
        if ( logger.isLoggable(Level.FINE) ) {
            logger.fine("\tStep 6. ReactiveObject is transited to state: [" + this.getToState() + "]");
        }
    }

    public void logStep6_1SetupNextStateStart() {
        if ( logger.isLoggable(Level.FINE) ) {
            logger.fine("\tStep 6. Set next state to reactiveObject.");
        }
    }

    public void logStep7Callback() {
        if ( logger.isLoggable(Level.FINE) ) {
            logger.fine("\tStep 7. Start Callback after state change from : " + this.getFromState() + " => to : " + this.getToState());
        }
    }

    public void logStep8FireLifecycleEvents() {
        if ( logger.isLoggable(Level.FINE) ) {
            logger.fine("\tStep 8. Start fire state change event.");
        }
    }

    public Unlockable popUnlockable() {
        return lockedRelatedObjectStack.pop();
    }

    @Override
    public void pushUnlockable(Unlockable unlockable) {
        this.lockedRelatedObjectStack.push(unlockable);
    }

    public void setFailureCause(Throwable failureCause) {
        this.success = false;
        this.failureCause = failureCause;
    }

    public void setFromState(String fromState) {
        this.fromState = fromState;
    }

    public void setSuccess(boolean b) {
        this.success = b;
        this.failureCause = null;
    }

    public void setToState(String nextState) {
        this.nextState = nextState;
    }

    public void setTransition(String transition) {
        this.transition = transition;
    }

    public void setTransitionType(TransitionTypeEnum transitionType) {
        this.transitionType = transitionType;
    }

    public void setupNextState(final StateMachineObject<?> stateMachine) {
        this.logStep6_1SetupNextStateStart();
        final String stateName = stateMachine.transitToNextState(this.getTarget(), this.getTransitionKey());
        this.setToState(stateName);
        this.logStep6_2SetupNextStateFinsihed();
    }

    public void validateTransition(StateMachineObject<?> stateMachine) {
        logStep2validateTransition();
        final String fromState = this.getFromState();
        final Object transitionKey = this.getTransitionKey();
        final TransitionMetadata transition;
        final V target = this.getTarget();
        transition = stateMachine.validateTransition(target, fromState, transitionKey);
        this.setTransitionType(transition.getType());
        this.setTransition(transition.getDottedPath().getName());
    }
}