package net.madz.lifecycle.engine;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.madz.lifecycle.LifecycleLockStrategry;
import net.madz.lifecycle.annotations.Function;
import net.madz.lifecycle.annotations.Functions;
import net.madz.lifecycle.annotations.LifecycleLock;
import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.annotations.state.End;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.verification.VerificationException;

import org.junit.BeforeClass;

public class LifecycleLockTestMetadata extends EngineTestBase {

    @BeforeClass
    public static void setup() throws VerificationException {
        registerMetaFromClass(LifecycleLockTestMetadata.class);
    }

    @StateMachine
    static interface LockingStateMachine {

        @StateSet
        static interface States {

            @Initial
            @Function(transition = LockingStateMachine.Transitions.Start.class, value = Started.class)
            static interface Created {}
            @Functions({ @Function(transition = LockingStateMachine.Transitions.Stop.class, value = Stopped.class),
                    @Function(transition = LockingStateMachine.Transitions.Cancel.class, value = Canceled.class) })
            static interface Started {}
            @End
            static interface Stopped {}
            @End
            static interface Canceled {}
        }
        @TransitionSet
        static interface Transitions {

            static interface Start {}
            static interface Stop {}
            static interface Cancel {}
        }
    }
    @LifecycleMeta(LockingStateMachine.class)
    static class SynchronizedLockingReactiveObject extends ReactiveObject implements ILockingReactiveObject {

        public SynchronizedLockingReactiveObject() {
            initialState(LockingStateMachine.States.Created.class.getSimpleName());
        }

        private volatile int counter = 0;

        /*
         * (non-Javadoc)
         * 
         * @see net.madz.lifecycle.engine.ILockingReactiveObject#getCounter()
         */
        @Override
        public int getCounter() {
            return counter;
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.madz.lifecycle.engine.ILockingReactiveObject#start()
         */
        @Override
        @Transition
        public synchronized void start() {
            counter++;
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.madz.lifecycle.engine.ILockingReactiveObject#stop()
         */
        @Override
        @Transition
        public synchronized void stop() {
            counter++;
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.madz.lifecycle.engine.ILockingReactiveObject#cancel()
         */
        @Override
        @Transition
        public synchronized void cancel() {
            counter++;
        }
    }
    public static class SimpleLock implements LifecycleLockStrategry {

        private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        @Override
        public void lockRead(Object reactiveObject) {
            lock.readLock().lock();
        }

        @Override
        public void unlockRead(Object targetReactiveObject) {
            lock.readLock().unlock();
        }

        @Override
        public void lockWrite(Object reactiveObject) {
            lock.writeLock().lock();
        }

        @Override
        public void unlockWrite(Object targetReactiveObject) {
            lock.writeLock().unlock();
        }
    }
    @LifecycleMeta(LockingStateMachine.class)
    @LifecycleLock(SimpleLock.class)
    static class SimpleLockingReactiveObject extends ReactiveObject implements ILockingReactiveObject {

        public SimpleLockingReactiveObject() {
            initialState(LockingStateMachine.States.Created.class.getSimpleName());
        }

        private volatile int counter = 0;

        public int getCounter() {
            return counter;
        }

        @Transition
        public void start() {
            counter++;
        }

        @Transition
        public void stop() {
            counter++;
        }

        @Transition
        public void cancel() {
            counter++;
        }
    }
}
