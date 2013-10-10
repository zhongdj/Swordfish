package net.madz.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassUtils {

    private static final String SETTER_PREFIX = "set";

    private ClassUtils() {
    }

    public static Method findMethodThroughClassHierarchy(Class<?> cl, String name) {
        try {
            return cl.getDeclaredMethod(name);
        } catch (SecurityException e) {
            throw new IllegalStateException(e);
        } catch (NoSuchMethodException e) {
            if ( null != cl.getSuperclass() ) {
                return findMethodThroughClassHierarchy(cl.getSuperclass(), name);
            } else {
                return null;
            }
        }
    }

    public static Method findMethodThroughClassHierarchy(Class<?> cl, String name, Class<?>... args) {
        try {
            return cl.getDeclaredMethod(name, args);
        } catch (SecurityException e) {
            throw new IllegalStateException(e);
        } catch (NoSuchMethodException e) {
            if ( null != cl.getSuperclass() ) {
                return findMethodThroughClassHierarchy(cl.getSuperclass(), name, args);
            } else {
                return null;
            }
        }
    }

    public static Field findFieldThroughClassHierarchy(Class<?> cl, String name) throws NoSuchFieldException {
        try {
            return cl.getDeclaredField(name);
        } catch (SecurityException e) {
            throw new IllegalStateException(e);
        } catch (NoSuchFieldException e) {
            if ( null != cl.getSuperclass() ) {
                return findFieldThroughClassHierarchy(cl.getSuperclass(), name);
            } else {
                throw e;
            }
        }
    }

    public static String getSetter(String name) {
        if ( null == name || 0 >= name.trim().length() ) {
            throw new IllegalArgumentException();
        }
        name = name.trim();
        if ( 1 == name.length() ) {
            return SETTER_PREFIX + name.substring(0).toUpperCase();
        } else {
            return SETTER_PREFIX + name.substring(0, 1).toUpperCase() + name.substring(1);
        }
    }

    public static <T> Field[] getAllFields(Class<T> toClazz) {
        ArrayList<Field> result = new ArrayList<Field>();
        List<Field> fieldList = Arrays.asList(toClazz.getDeclaredFields());
        result.addAll(fieldList);
        Class<?> cl = toClazz.getSuperclass();
        while ( null != cl && Object.class != cl ) {
            fieldList = Arrays.asList(cl.getDeclaredFields());
            result.addAll(fieldList);
            cl = cl.getSuperclass();
        }
        return result.toArray(new Field[fieldList.size()]);
    }

    public static String getGetter(Field field) {
        String name = field.getName();
        final String getterPrefix;
        if ( Boolean.class.isAssignableFrom(field.getType()) ) {
            getterPrefix = "is";
        } else {
            getterPrefix = "get";
        }
        return getGetter(name, getterPrefix);
    }

    public static String getGetter(String name) {
        return getGetter(name, null);
    }

    private static String getGetter(String name, String getterPrefix) {
        if ( null == getterPrefix ) {
            getterPrefix = "get";
        }
        if ( 1 == name.length() ) {
            return getterPrefix + name.substring(0).toUpperCase();
        } else {
            return getterPrefix + name.substring(0, 1).toUpperCase() + name.substring(1);
        }
    }
}
