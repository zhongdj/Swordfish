package net.madz.lifecycle.impl;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.madz.lifecycle.annotations.typed.ITypedStateChangeListener;
import net.madz.lifecycle.annotations.typed.TypedStateContext;

public class StateChangeListenerHub {

    public final static StateChangeListenerHub INSTANCE = new StateChangeListenerHub();
    private final ArrayList<ITypedStateChangeListener> listeners = new ArrayList<ITypedStateChangeListener>();
    private final ExecutorService executor = Executors.newCachedThreadPool(new ThreadFactory() {

        int counter = 0;

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "State Change Notify Thread --" + (++counter));
        }
    });

    public synchronized void registerListener(ITypedStateChangeListener listener) {
        if (listeners.contains(listener)) {
            return;
        }
        listeners.add(listener);
    }

    public synchronized void removeListener(ITypedStateChangeListener listener) {
        listeners.remove(listener);
    }

    public void notify(final TypedStateContext<?, ?> context) {
        final ArrayList<ITypedStateChangeListener> imageList = new ArrayList<ITypedStateChangeListener>(listeners);

        for (final ITypedStateChangeListener listener : imageList) {
            executor.submit(new Runnable() {

                @Override
                public void run() {
                    try {
                        listener.onStateChanged(context);
                    } catch (Throwable t) {
                        Logger.getAnonymousLogger().log(Level.SEVERE, "state change notify failed with listener" + listener.getClass().getName(), t);
                    }
                }
            });
        }
    }
}
