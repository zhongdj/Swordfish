package net.madz.bcel.intercept;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Interceptor<V> {

    private static final Logger logger = Logger.getLogger("Lifecycle Framework");
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
        } catch (Throwable e) {
            handleException(context, e);
            throw e;
        } finally {
            cleanup(context);
        }
    }

    protected void handleException(InterceptContext<V> context, Throwable e) {
        if ( e instanceof RuntimeException ) {
            throw (RuntimeException) e;
        }
        if ( logger.isLoggable(Level.FINE) ) {
            logger.fine("intercepting with:" + getClass().getName() + " @handleException");
        }
    }

    protected void cleanup(InterceptContext<V> context) {
        if ( logger.isLoggable(Level.FINE) ) {
            logger.fine("intercepting with :" + getClass().getName() + " @cleanup");
        }
    }

    protected void postExec(InterceptContext<V> context) {
        if ( logger.isLoggable(Level.FINE) ) {
            logger.fine("intercepting with :" + getClass().getName() + " @postExec");
        }
    }

    protected void preExec(InterceptContext<V> context) {
        if ( logger.isLoggable(Level.FINE) ) {
            logger.fine("intercepting with :" + getClass().getName() + " @preExec");
        }
    }
}