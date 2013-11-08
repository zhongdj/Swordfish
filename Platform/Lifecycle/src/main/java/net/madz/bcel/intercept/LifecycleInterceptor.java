package net.madz.bcel.intercept;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map.Entry;

import net.madz.lifecycle.annotations.relation.Relation;

public class LifecycleInterceptor<V> extends Interceptor<V> {

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
        System.out.println("Intercepting....LifecycleInterceptor is doing postExec ...");
    }

    @Override
    protected void preExec(InterceptContext<V> context) {
        super.preExec(context);
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
        System.out.println("Intercepting....LifecycleInterceptor is doing preExec ...");
        Annotation[] annotation = context.getAnnotation();
        System.out.println("Found Annotations: ");
        for ( Annotation annotation2 : annotation ) {
            System.out.println(annotation2);
        }
    }
}