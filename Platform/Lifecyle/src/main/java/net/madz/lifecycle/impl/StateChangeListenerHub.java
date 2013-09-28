package net.madz.lifecycle.impl;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.madz.lifecycle.IStateChangeListener;
import net.madz.lifecycle.StateContext;

public class StateChangeListenerHub {

    public final static StateChangeListenerHub INSTANCE = new StateChangeListenerHub();
    private final ArrayList<IStateChangeListener> listeners = new ArrayList<IStateChangeListener>();
    private final ExecutorService executor = Executors.newCachedThreadPool(new ThreadFactory() {

        int counter = 0;

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "State Change Notify Thread --" + (++counter));
        }
    });

    public synchronized void registerListener(IStateChangeListener listener) {
        if (listeners.contains(listener)) {
            return;
        }
        listeners.add(listener);
    }

    public synchronized void removeListener(IStateChangeListener listener) {
        listeners.remove(listener);
    }

    public void notify(final StateContext<?, ?> context) {
        final ArrayList<IStateChangeListener> imageList = new ArrayList<IStateChangeListener>(listeners);

        for (final IStateChangeListener listener : imageList) {
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
