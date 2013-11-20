package net.madz.bcel.intercept;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CallableInterceptor<V> extends Interceptor<V> {

    private static final Logger logger = Logger.getLogger("Lifecycle Framework");

    @Override
    public V intercept(InterceptContext<V> context, Callable<V> callable) {
        try {
            if ( logger.isLoggable(Level.FINE) ) {
                logger.fine("intercepting with: " + getClass().getName() + " @intercept");
            }
            return callable.call();
        } catch (Exception e) {
            if ( e instanceof RuntimeException ) {
                throw (RuntimeException) e;
            } else {
                throw new IllegalStateException(e);
            }
        }
    }
}