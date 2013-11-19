package net.madz.lifecycle.meta.instance;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.madz.lifecycle.meta.MetaObject;
import net.madz.lifecycle.meta.template.RelationMetadata;

public interface RelationObject extends MetaObject<RelationObject, RelationMetadata> {

    ReadAccessor<?> getEvaluator();

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
}
