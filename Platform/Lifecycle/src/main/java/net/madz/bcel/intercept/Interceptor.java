package net.madz.bcel.intercept;

import java.util.concurrent.Callable;

public abstract class Interceptor<V> {

    private Interceptor<V> next;

    public Interceptor() {}

    public Interceptor(Interceptor<V> next) {
        super();
        this.next = next;
    }

    public V intercept(InterceptContext<V> context, Callable<V> callable) {
        try {
            preExec(context);
            V result = next.intercept(context, callable);
            postExec(context);
            return result;
        } catch (Exception e) {
            handleException(e);
            throw new IllegalStateException(e);
        } finally {
            cleanup(context);
        }
    }

    protected void handleException(Exception e) {
        if ( e instanceof RuntimeException ) {
            throw (RuntimeException) e;
        }
        System.out.println("intercepting with :" + getClass().getName() + " @handleException");
    }

    protected void cleanup(InterceptContext<V> context) {
        System.out.println("intercepting with :" + getClass().getName() + " @cleanup");
    }

    protected void postExec(InterceptContext<V> context) {
        System.out.println("intercepting with :" + getClass().getName() + " @postExec");
    }

    protected void preExec(InterceptContext<V> context) {
        System.out.println("intercepting with :" + getClass().getName() + " @preExec");
    }
}