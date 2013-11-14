package net.madz.bcel.intercept;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import net.madz.lifecycle.annotations.Null;
import net.madz.lifecycle.annotations.Transition;
import net.madz.util.StringUtil;

public class InterceptContext<V> {

    private final Annotation[] annotation;
    private final Class<?> klass;
    private final Method method;
    private final Object target;
    private final Object[] arguments;
    private String fromState;
    private String nextState;
    private String transition;
    private Throwable failureCause;
    private long startTime;
    private long endTime;

    public InterceptContext(Class<?> klass, Object target, String methodName, Class<?>[] argsType, Object[] arguments) {
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
        System.out.println("Found Intercept Point: " + klass + "." + methodName + "( " + sb.toString() + " )");
        System.out.println("Intercepting....instatiating InterceptContext ...");
    }

    public String getFromState() {
        return fromState;
    }

    public void setFromState(String fromState) {
        this.fromState = fromState;
    }

    public String getNextState() {
        return nextState;
    }

    public void setNextState(String nextState) {
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
        this.failureCause = failureCause;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
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

    public Object getTarget() {
        return target;
    }

    public Object getTranstionKey() {
        final Class<?> keyClass = method.getAnnotation(Transition.class).value();
        if ( Null.class.equals(keyClass) ) {
            return StringUtil.toUppercaseFirstCharacter(method.getName());
        } else {
            return keyClass;
        }
    }

    public Interceptor<V> createInterceptorChain() {
        return new LifecycleInterceptor<V>(new CallableInterceptor<V>());
    }

    protected Method findMethod(Class<?> klass, String methodName, Class<?>[] classes) {
        try {
            return klass.getDeclaredMethod(methodName, classes);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}