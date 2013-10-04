package net.madz.lifecycle.meta;

public interface Template<T> {

    T newInstance(Class<?> clazz);
}
