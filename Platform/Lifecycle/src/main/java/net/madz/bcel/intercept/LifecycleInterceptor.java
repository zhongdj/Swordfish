package net.madz.bcel.intercept;

import java.lang.annotation.Annotation;


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
        System.out.println("Intercepting....LifecycleInterceptor is doing preExec ...");
        Annotation[] annotation = context.getAnnotation();
        System.out.println("Found Annotations: ");
        for ( Annotation annotation2 : annotation ) {
            System.out.println(annotation2);
        }
    }
}