package net.madz.lifecycle.meta;

import java.lang.reflect.Field;

import net.madz.lifecycle.meta.MetaObject.ReadAccessor;

public final class FieldEvaluator<T> implements MetaObject.ReadAccessor<T> {

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