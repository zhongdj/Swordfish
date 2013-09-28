package net.madz.core.biz;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class BOFactory {

    public static <S extends T, T extends IBizObject<?>> S create(Class<T> proxyClass) {
        BOProxy annotation = proxyClass.getAnnotation(BOProxy.class);
        if ( proxyClass.isAssignableFrom(annotation.value()) ) {
            @SuppressWarnings("unchecked")
            Class<S> clz = (Class<S>) annotation.value();
            try {
                return clz.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("@BOProxy(value = " + annotation.value().getName()
                    + "): value is not subclass of " + proxyClass.getName());
        }
    }

    public static <E extends Object, S extends T, T extends IBizObject<E>> S create(Class<T> proxyClass, E entity) {
        BOProxy annotation = proxyClass.getAnnotation(BOProxy.class);
        if ( proxyClass.isAssignableFrom(annotation.value()) ) {
            @SuppressWarnings("unchecked")
            Class<S> clz = (Class<S>) annotation.value();
            try {
                final Constructor<S> constructor = clz.getConstructor(entity.getClass());
                return constructor.newInstance(entity);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("@BOProxy(value = " + annotation.value().getName()
                    + "): value is not subclass of " + proxyClass.getName());
        }
    }

    @SuppressWarnings("unchecked")
    public static <E extends Object, S extends T, T extends IBizObject<E>> List<S> create(Class<T> proxyClass,
            List<E> entities) {
        final List<S> result = new ArrayList<>(entities.size());
        for ( E entity : entities ) {
            result.add((S) create(proxyClass, entity));
        }
        return result;
    }

    private BOFactory() {
    }
}
