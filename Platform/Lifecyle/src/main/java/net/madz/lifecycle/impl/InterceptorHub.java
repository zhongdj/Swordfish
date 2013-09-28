package net.madz.lifecycle.impl;

import java.util.ArrayList;

import net.madz.lifecycle.IStateChangeInterceptor;
import net.madz.lifecycle.StateContext;

public class InterceptorHub {

    public final static InterceptorHub INSTANCE = new InterceptorHub();

    private ArrayList<IStateChangeInterceptor> interceptors = new ArrayList<IStateChangeInterceptor>();

    public synchronized void registerInterceptor(IStateChangeInterceptor interceptor) {
        if (interceptors.contains(interceptor)) {
            return;
        } else {
            interceptors.add(interceptor);
        }
    }

    public synchronized void removeInterceptor(IStateChangeInterceptor interceptor) {
        interceptors.remove(interceptor);
    }

    public void intercept(StateContext<?, ?> context) {
        final ArrayList<IStateChangeInterceptor> imageList = new ArrayList<IStateChangeInterceptor>(interceptors);
        for (IStateChangeInterceptor interceptor : imageList) {
            interceptor.interceptStateChange(context);
        }
    }
}
