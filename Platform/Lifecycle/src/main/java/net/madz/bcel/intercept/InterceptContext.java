package net.madz.bcel.intercept;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.madz.lifecycle.annotations.Null;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.meta.template.TransitionMetadata.TransitionTypeEnum;
import net.madz.util.StringUtil;

public class InterceptContext<V, R> implements UnlockableStack {

    private static Logger logger = Logger.getLogger("Lifecycle Framework");
    private final Annotation[] annotation;
    private final Class<?> klass;
    private final Method method;
    private final V target;
    private final Object[] arguments;
    private String fromState;
    private String nextState;
    private String transition;
    private Throwable failureCause;
    private long startTime;
    private long endTime;
    private TransitionTypeEnum transitionType;
    private boolean success;
    private final Stack<Unlockable> lockedRelatedObjectStack = new Stack<>();

    public InterceptContext(Class<?> klass, V target, String methodName, Class<?>[] argsType, Object[] arguments) {
        super();
        this.klass = klass;
        this.method = findMethod(klass, methodName, argsType);
        this.annotation = method.getAnnotations();
        this.target = target;
        if ( null == arguments ) {
            this.arguments = new Object[0];
        } else {
            this.arguments = arguments;
        }
        StringBuilder sb = new StringBuilder(" ");
        for ( Object o : this.arguments ) {
            sb.append(String.valueOf(o)).append(" ");
        }
        startTime = System.currentTimeMillis();
        if ( logger.isLoggable(Level.FINE) ) {
            logger.fine("Found Intercept Point: " + klass + "." + methodName + "( " + sb.toString() + " )");
            logger.fine("Intercepting....instatiating InterceptContext ...");
        }
    }

    public String getFromState() {
        return fromState;
    }

    public void setFromState(String fromState) {
        this.fromState = fromState;
    }

    public String getToState() {
        return nextState;
    }

    public void setToState(String nextState) {
        this.nextState = nextState;
    }

    public String getTransition() {
        return transition;
    }

    public void setTransition(String transition) {
        this.transition = transition;
    }

    public Throwable getFailureCause() {
        return failureCause;
    }

    public void setFailureCause(Throwable failureCause) {
        this.success = false;
        this.failureCause = failureCause;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void end() {
        this.endTime = System.currentTimeMillis();
    }

    public Object[] getArguments() {
        return this.arguments;
    }

    public Annotation[] getAnnotation() {
        return annotation;
    }

    public Class<?> getKlass() {
        return klass;
    }

    public Method getMethod() {
        return method;
    }

    public V getTarget() {
        return target;
    }

    public Object getTransitionKey() {
        final Class<?> keyClass = method.getAnnotation(Transition.class).value();
        if ( Null.class.equals(keyClass) ) {
            return StringUtil.toUppercaseFirstCharacter(method.getName());
        } else {
            return keyClass;
        }
    }

    public Interceptor<V, R> createInterceptorChain() {
        return new LifecycleInterceptor<V, R>(new CallableInterceptor<V, R>());
    }

    protected Method findMethod(Class<?> klass, String methodName, Class<?>[] classes) {
        try {
            return klass.getDeclaredMethod(methodName, classes);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public TransitionTypeEnum getTransitionType() {
        return transitionType;
    }

    public void setTransitionType(TransitionTypeEnum transitionType) {
        this.transitionType = transitionType;
    }

    public void setSuccess(boolean b) {
        this.success = b;
        this.failureCause = null;
    }

    public boolean isSuccess() {
        return success;
    }

    public Unlockable popUnlockable() {
        return lockedRelatedObjectStack.pop();
    }

    @Override
    public void pushUnlockable(Unlockable unlockable) {
        this.lockedRelatedObjectStack.push(unlockable);
    }

    @Override
    public boolean isEmpty() {
        return this.lockedRelatedObjectStack.isEmpty();
    }
}