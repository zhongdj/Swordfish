package net.madz.lifecycle.meta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.madz.lifecycle.meta.MetaObject.ReadAccessor;

public final class PropertyEvaluator<T> implements MetaObject.ReadAccessor<T> {

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