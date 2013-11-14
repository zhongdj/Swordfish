package net.madz.bcel.intercept;

import java.util.concurrent.Callable;

public class CallableInterceptor<V> extends Interceptor<V> {

    @Override
    public V intercept(InterceptContext<V> context, Callable<V> callable) {
        try {
            System.out.println("intercepting with: " + getClass().getName() + " @intercept");
            return callable.call();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}