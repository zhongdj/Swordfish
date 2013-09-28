package net.madz.event;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface LifeCycleEventListeners {
    Class<? extends ILifeCycleEventListener>[] value() default {};
}
