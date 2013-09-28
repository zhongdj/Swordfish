package net.madz.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.eventbus.EventBus;

public abstract class LifeCycleEventUtils {

    private static final EventBus bus = new EventBus();
    
    static {
        final LifeCycleEventListeners annotation = LifeCycleEvent.class.getAnnotation(LifeCycleEventListeners.class);
        Class<? extends ILifeCycleEventListener>[] listenerClasses = annotation.value();
        for ( Class<? extends ILifeCycleEventListener> listenerClass : listenerClasses ) {
            try {
                bus.register(listenerClass.newInstance());
            } catch (Exception e) {
                Logger.getAnonymousLogger().log(Level.SEVERE, "Failed to create instance of class: " + listenerClass.getName(), e);
            }
        }
    }

    public static void notify(LifeCycleEvent event) {
        bus.post(event);
    }
}
