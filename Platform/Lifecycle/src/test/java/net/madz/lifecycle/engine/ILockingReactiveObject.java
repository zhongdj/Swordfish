package net.madz.lifecycle.engine;

interface ILockingReactiveObject {

    public abstract int getCounter();

    public abstract void start();

    public abstract void stop();

    public abstract void cancel();
}