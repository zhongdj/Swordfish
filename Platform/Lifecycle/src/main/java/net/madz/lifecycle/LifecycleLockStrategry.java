package net.madz.lifecycle;

public interface LifecycleLockStrategry {

    void lock(Object reactiveObject);

    void lockParent(Object parentReactiveObject);

    void lockRelative(Object relatedReactiveObject);

    void unlock(Object targetReactiveObject);

    void unlockParent(Object parentReactiveObject);

    void unlockRelative(Object relative);
}
