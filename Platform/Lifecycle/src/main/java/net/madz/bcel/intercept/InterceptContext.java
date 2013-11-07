package net.madz.bcel.intercept;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class InterceptContext<V> {

    private final Annotation[] annotation;
    private final Class<?> klass;
    private final Method method;
    private final Object target;

    public InterceptContext(Class<?> klass, Object target, String methodName, Class<?>[] argsType) {
        super();
        this.klass = klass;
        this.method = findMethod(klass, methodName, argsType);
        this.annotation = method.getAnnotations();
        this.target = target;
        System.out.println("Intercepting....instatiating InterceptContext ...");
    }
    public InterceptContext(Class<?> klass, Object target, Method interceptMethod) {
        super();
        this.klass = klass;
        this.method = interceptMethod;
        this.annotation = method.getAnnotations();
        this.target = target;
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