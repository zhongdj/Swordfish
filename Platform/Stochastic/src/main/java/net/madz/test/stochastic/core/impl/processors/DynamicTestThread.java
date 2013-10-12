package net.madz.test.stochastic.core.impl.processors;

public final class DynamicTestThread extends Thread {

    private final int number;

    public DynamicTestThread(final int number, final Runnable runnable) {
        super(runnable);
        this.number = number;
        setName("Dynamic Test Executor Thread-" + this.number);
    }

    public int getNumber() {
        return this.number;
    }
}