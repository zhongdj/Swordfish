package net.madz.test.stochastic.utilities;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClassUtils {

    @SuppressWarnings("rawtypes")
    public static <T> void setProperty(final T t, String propertyName, String propertyValue) {
        Field field = null;
        try {
            Class clz = t.getClass();
            for ( ; null == field && null != clz; clz = clz.getSuperclass() ) {
                try {
                    field = clz.getDeclaredField(propertyName);
                } catch (Exception ex) {
                }
            }
            field.setAccessible(true);
            if ( field.getType().equals(String.class) ) {
                field.set(t, propertyValue);
            } else if ( field.getType().isEnum() ) {
                final Method m = field.getType().getMethod("valueOf", String.class);
                final Object enumValue = m.invoke(t, propertyValue);
                field.set(t, enumValue);
            } else if ( field.getType().equals(Integer.class) ) {
                field.set(t, Integer.valueOf(propertyValue));
            } else if ( field.getType().equals(Float.class) ) {
                field.set(t, Float.valueOf(propertyValue));
            } else if ( field.getType().equals(Double.class) ) {
                field.set(t, Double.valueOf(propertyValue));
            } else if ( field.getType().equals(Timestamp.class) ) {
                final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                final Date date = dateFormatter.parse(propertyValue);
                field.set(t, new Timestamp(date.getTime()));
            } else if ( field.getType().equals(Date.class) ) {
                final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                final Date date = dateFormatter.parse(propertyValue);
                field.set(t, date);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            if ( null != field ) {
                field.setAccessible(false);
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void setProperty(final Object t, String propertyName, Object propertyValue) {
        Field field = null;
        try {
            Class clz = t.getClass();
            for ( ; null == field && null != clz; clz = clz.getSuperclass() ) {
                try {
                    field = clz.getDeclaredField(propertyName);
                } catch (Exception ex) {
                }
            }
            final boolean update;
            if ( field.isAccessible() ) {
                update = false;
            } else {
                update = true;
                field.setAccessible(true);
            }
            if ( field.getType().isEnum() && propertyValue instanceof String ) {
                final Class<? extends Enum> type = (Class<? extends Enum>) field.getType();
                propertyValue = Enum.valueOf(type, (String) propertyValue);
            }
            field.set(t, propertyValue);
            if ( update ) {
                field.setAccessible(false);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            if ( null != field ) {
                field.setAccessible(false);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public static Object getProperty(Object instance, String fieldName) {
        Class clz = instance.getClass();
        Field field = null;
        for ( ; null == field && null != clz; clz = clz.getSuperclass() ) {
            try {
                field = clz.getDeclaredField(fieldName);
            } catch (Exception ignored) {
            }
        }
        final boolean update;
        if ( !field.isAccessible() ) {
            update = true;
            field.setAccessible(true);
        } else {
            update = false;
        }
        try {
            return field.get(instance);
        } catch (Exception ignored) {
            throw new IllegalStateException(ignored);
        } finally {
            if ( update ) {
                field.setAccessible(false);
            }
        }
    }
}
