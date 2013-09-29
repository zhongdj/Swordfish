package net.madz.lifecycle.impl;

import java.util.ArrayList;

import net.madz.lifecycle.annotations.typed.ITypedStateChangeInterceptor;
import net.madz.lifecycle.annotations.typed.TypedStateContext;

public class InterceptorHub {

    public final static InterceptorHub INSTANCE = new InterceptorHub();

    private ArrayList<ITypedStateChangeInterceptor> interceptors = new ArrayList<ITypedStateChangeInterceptor>();

    public synchronized void registerInterceptor(ITypedStateChangeInterceptor interceptor) {
        if (interceptors.contains(interceptor)) {
            return;
        } else {
            interceptors.add(interceptor);
        }
    }

    public synchronized void removeInterceptor(ITypedStateChangeInterceptor interceptor) {
        interceptors.remove(interceptor);
    }

    public void intercept(TypedStateContext<?, ?> context) {
        final ArrayList<ITypedStateChangeInterceptor> imageList = new ArrayList<ITypedStateChangeInterceptor>(interceptors);
        for (ITypedStateChangeInterceptor interceptor : imageList) {
            interceptor.interceptStateChange(context);
        }
    }
}
