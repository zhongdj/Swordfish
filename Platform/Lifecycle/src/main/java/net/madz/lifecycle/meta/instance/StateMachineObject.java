package net.madz.lifecycle.meta.instance;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.madz.bcel.intercept.InterceptContext;
import net.madz.lifecycle.LifecycleLockStrategry;
import net.madz.lifecycle.StateConverter;
import net.madz.lifecycle.meta.MetaObject;
import net.madz.lifecycle.meta.template.StateMachineMetadata;

public interface StateMachineObject extends MetaObject<StateMachineObject, StateMachineMetadata> {

    TransitionObject[] getTransitionSet();

    boolean hasTransition(Object transitionKey);

    TransitionObject getTransition(Object transitionKey);

    StateObject[] getStateSet();

    StateObject getState(Object stateKey);

    StateAccessor<String> getStateAccessor();

    public static interface ReadAccessor<T> {

        T read(Object reactiveObject);
    }
    public final static class FieldEvaluator<T> implements ReadAccessor<T> {

        private final Field objField;

        public FieldEvaluator(Field objField) {
            this.objField = objField;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T read(Object reactiveObject) {
            final boolean accessible = objField.isAccessible();
            try {
                if ( !accessible ) {
                    objField.setAccessible(true);
                }
                return (T) objField.get(reactiveObject);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            } finally {
                if ( !accessible ) {
                    objField.setAccessible(false);
                }
            }
        }
    }
    public final static class PropertyEvaluator<T> implements ReadAccessor<T> {

        private final Method getter;

        public PropertyEvaluator(Method objMethod) {
            this.getter = objMethod;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T read(Object reactiveObject) {
            final boolean accessible = getter.isAccessible();
            try {
                if ( !accessible ) {
                    getter.setAccessible(true);
                    return (T) getter.invoke(reactiveObject);
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            } finally {
                if ( !accessible ) {
                    getter.setAccessible(false);
                }
            }
            return null;
        }
    }
    public static interface StateAccessor<T> extends ReadAccessor<T> {

        void write(Object reactiveObject, T state);
    }
    public final static class FieldStateAccessor<T> implements StateAccessor<T> {

        private final Field stateField;

        public FieldStateAccessor(Field stateField) {
            this.stateField = stateField;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T read(Object reactiveObject) {
            final boolean accessible = stateField.isAccessible();
            try {
                if ( !accessible ) {
                    stateField.setAccessible(true);
                }
                return (T) stateField.get(reactiveObject);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            } finally {
                if ( !accessible ) {
                    stateField.setAccessible(false);
                }
            }
        }

        @Override
        public void write(Object reactiveObject, T state) {
            final boolean accessible = stateField.isAccessible();
            try {
                if ( !accessible ) {
                    stateField.setAccessible(true);
                }
                stateField.set(reactiveObject, state);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            } finally {
                if ( !accessible ) {
                    stateField.setAccessible(false);
                }
            }
        }
    }
    public final static class PropertyAccessor<T> implements StateAccessor<T> {

        private final Method getter;
        private final Method setter;

        public PropertyAccessor(Method getter, Method setter) {
            this.getter = getter;
            this.setter = setter;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T read(Object reactiveObject) {
            try {
                return (T) getter.invoke(reactiveObject);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        public void write(Object reactiveObject, T state) {
            try {
                setter.setAccessible(true);
                setter.invoke(reactiveObject, state);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            } finally {
                setter.setAccessible(false);
            }
        }
    }
    public final static class ConverterAccessor<T> implements StateAccessor<String> {

        private final StateConverter<T> stateConverter;
        private final StateAccessor<T> rawAccessor;

        public ConverterAccessor(StateConverter<T> stateConverter, StateAccessor<T> rawAccessor) {
            this.stateConverter = stateConverter;
            this.rawAccessor = rawAccessor;
        }

        @Override
        public String read(Object reactiveObject) {
            return stateConverter.toState(rawAccessor.read(reactiveObject));
        }

        @Override
        public void write(Object reactiveObject, String state) {
            rawAccessor.write(reactiveObject, stateConverter.fromState(state));
        }
    }

    String evaluateState(Object target);

    void setTargetState(Object target, String state);

    String getNextState(Object target, Object transtionKey);

    void validateValidWhiles(final InterceptContext context);

    void validateInboundWhiles(final InterceptContext context);

    boolean evaluateConditionBeforeTransition(Object transtionKey);

    LifecycleLockStrategry getLifecycleLockStrategy();

    Object evaluateParent(Object target);

    Object[] evaluateRelatives(Object target);

    StateMachineObject getParentStateMachine();

    StateMachineObject getRelatedStateMachine(Class<?> relativeClass);
}
