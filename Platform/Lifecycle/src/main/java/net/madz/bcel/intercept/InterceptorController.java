package net.madz.bcel.intercept;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InterceptorController<V> {

    private static Logger logger = Logger.getLogger("Lifecycle Framework");

    public V exec(InterceptContext<V> context, Callable<V> callable) throws Throwable {
        if ( logger.isLoggable(Level.FINE) ) {
            logger.fine("Intercepting....InterceptorController is doing exec ...");
        }
        Interceptor<V> interceptorChain = context.createInterceptorChain();
        return interceptorChain.intercept(context, callable);
    }
}