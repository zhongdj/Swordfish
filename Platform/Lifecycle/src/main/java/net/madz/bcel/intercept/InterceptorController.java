package net.madz.bcel.intercept;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InterceptorController<V, R> {

    private static Logger logger = Logger.getLogger("Lifecycle Framework");

    public R exec(InterceptContext<V ,R> context, Callable<R> callable) {
        if ( logger.isLoggable(Level.FINE) ) {
            logger.fine("Intercepting....InterceptorController is doing exec ...");
        }
        Interceptor<V, R> interceptorChain = context.createInterceptorChain();
        return interceptorChain.intercept(context, callable);
    }
}