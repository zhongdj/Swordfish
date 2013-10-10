package net.madz.core.biz;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.madz.core.entities.AbstractBaseEntity;

public class BOFactory {

    private static Logger logger = Logger.getLogger(BOFactory.class.getName());

    public static <E extends AbstractBaseEntity, T extends IBizObject<E>> T create(Class<T> proxyClass) {
        BOProxy annotation = proxyClass.getAnnotation(BOProxy.class);
        if ( proxyClass.isAssignableFrom(annotation.value()) ) {
            @SuppressWarnings("unchecked")
            Class<T> clz = (Class<T>) annotation.value();
            try {
                T bizObject = clz.newInstance();
                return wrap(proxyClass, bizObject);
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("@BOProxy(value = " + annotation.value().getName()
                    + "): value is not subclass of " + proxyClass.getName());
        }
    }

    public static <E extends AbstractBaseEntity, T extends IBizObject<E>> T create(Class<T> proxyClass, E entity) {
        BOProxy annotation = proxyClass.getAnnotation(BOProxy.class);
        if ( proxyClass.isAssignableFrom(annotation.value()) ) {
            @SuppressWarnings("unchecked")
            Class<T> clz = (Class<T>) annotation.value();
            try {
                final Constructor<T> constructor = clz.getConstructor(entity.getClass());
                return wrap(proxyClass, constructor.newInstance(entity));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("@BOProxy(value = " + annotation.value().getName()
                    + "): value is not subclass of " + proxyClass.getName());
        }
    }

    public static <E extends AbstractBaseEntity, T extends IBizObject<E>> List<T> create(Class<T> proxyClass,
            List<E> entities) {
        final List<T> result = new ArrayList<>(entities.size());
        for ( E entity : entities ) {
            result.add((T) create(proxyClass, entity));
        }
        return result;
    }

    @SuppressWarnings({ "unchecked" })
    private static <E extends AbstractBaseEntity, T extends IBizObject<E>> T wrap(final Class<T> proxyClass,
            final T object) {
        return (T) Proxy.newProxyInstance(object.getClass().getClassLoader(), new Class[] { proxyClass },
                new InvocationHandler() {

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        convertParameterToProxy(method, args);
                        // TransitionInvocationHandler tih = new
                        // TransitionInvocationHandler(object);
                        // Proxy.newProxyInstance(getClass().getClassLoader(),
                        // new Class[] { proxyClass }, tih);
                        return method.invoke(object, args);
                    }

                    private void convertParameterToProxy(Method method, Object[] args) {
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        for ( int i = 0; i < parameterTypes.length; i++ ) {
                            Class<?> c = parameterTypes[i];
                            if ( c.isInterface() && IBizObject.class.isAssignableFrom(c) ) {
                                Class<T> tClass = (Class<T>) c;
                                IBizObject<E> bo = (IBizObject<E>) args[i];
                                if ( AbstractBO.class.isAssignableFrom(args[i].getClass()) ) {
                                    args[i] = BOFactory.create(tClass, bo.get());
                                }
                            }
                        }
                    }
                });
    }

    private BOFactory() {
    }
}
