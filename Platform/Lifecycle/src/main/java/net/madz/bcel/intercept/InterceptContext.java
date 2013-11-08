package net.madz.bcel.intercept;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class InterceptContext<V> {

    private final Annotation[] annotation;
    private final Class<?> klass;
    private final Method method;
    private final Object target;
    private final Object[] arguments;

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
        System.out.println("Found Intercept Point: " + methodName + "( " + sb.toString() + " )");
        System.out.println("Intercepting....instatiating InterceptContext ...");
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