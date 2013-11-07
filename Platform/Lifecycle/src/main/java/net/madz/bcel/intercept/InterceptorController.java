package net.madz.bcel.intercept;


import java.util.concurrent.Callable;

public class InterceptorController<V> {

    public V exec(InterceptContext<V> context, Callable<V> callable) {
        System.out.println("Intercepting....InterceptorController is doing exec ...");
        Interceptor<V> interceptorChain = context.createInterceptorChain();
        return interceptorChain.intercept(context, callable);
    }
}