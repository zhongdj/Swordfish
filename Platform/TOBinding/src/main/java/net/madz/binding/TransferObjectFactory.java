/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.madz.binding;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.madz.binding.annotation.AccessTypeEnum;
import net.madz.binding.annotation.Binding;
import net.madz.binding.annotation.BindingTypeEnum;
import net.madz.util.ClassUtils;

/**
 * The class offers two methods. One method is used to create Sigle Transfer
 * Ojbect, the other is used to assembly a list of transfer object.
 * 
 * @author Barry
 */
public class TransferObjectFactory {

    private static ThreadLocal _cache = new ThreadLocal();

    // TODO [Tracy] [Done] [Add Method Comments] [Alt + Shift + J]
    /**
     * Assemble Transfer Objects for a list of business objects by invoking
     * createTransferObject(Class<T> toClazz, Object bizObject) method
     * 
     * @param <T>
     * @param list
     * @param clazz
     * @return Transfer Object List
     * @throws Exception
     */
    public static <T> List<T> assembleTransferObjectList(final Collection<?> list, final Class<T> clazz)
            throws Exception {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            final Callable<List<T>> callable = new Callable<List<T>>() {

                @Override
                public List<T> call() throws Exception {
                    return __assembleTransferObjectList(list, clazz);
                }
            };
            final Future<List<T>> future = executor.submit(callable);
            return future.get();
        } finally {
            executor.shutdownNow();
        }
    }

    /**
     * 
     * @out param <T>
     * @in param toClazz
     * @in param bizObject
     * @return Transfer Object instance
     * @throws Exception
     */
    public static <T> T createTransferObject(final Class<T> toClazz, final Object bizObject) throws Exception {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Callable<T> callable = new Callable<T>() {

                @Override
                public T call() throws Exception {
                    return __createTransferObject(toClazz, bizObject);
                }
            };
            Future<T> future = executor.submit(callable);
            return future.get();
        } finally {
            executor.shutdownNow();
        }
    }

    public static <T> Set<T> assembleTransferObjectSet(final Collection<?> list, final Class<T> clazz) throws Exception {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            final Callable<Set<T>> callable = new Callable<Set<T>>() {

                @Override
                public Set<T> call() throws Exception {
                    return __assembleTransferObjectSet(list, clazz);
                }
            };
            Future<Set<T>> future = executor.submit(callable);
            return future.get();
        } finally {
            executor.shutdownNow();
        }
    }

    private static <T> List<T> __assembleTransferObjectList(final Collection<?> list, final Class<T> clazz)
            throws Exception {
        final List<T> result = new ArrayList<T>();
        for ( Object object : list ) {
            try {
                T qto = __createTransferObject(clazz, object);
                result.add(qto);
            } catch (Exception ex) {
                Logger.getLogger(TransferObjectFactory.class.getName()).log(Level.SEVERE, null, ex);
                throw ex;
            }
        }
        return result;
    }

    private static <T> Set<T> __assembleTransferObjectSet(final Collection<?> list, final Class<T> clazz)
            throws Exception {
        final Set<T> result = new HashSet<T>();
        for ( Object object : list ) {
            try {
                T qto = __createTransferObject(clazz, object);
                result.add(qto);
            } catch (Exception ex) {
                Logger.getLogger(TransferObjectFactory.class.getName()).log(Level.SEVERE, null, ex);
                throw ex;
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static <T> ThreadLocal<Map<Class<T>, Map<String, T>>> getCache(Class<T> cl) {
        return _cache;
    }

    private static <T> T getCachedObject(Class<T> cl, String id) {
        final ThreadLocal<Map<Class<T>, Map<String, T>>> cache = getCache(cl);
        if ( null == cache.get() ) {
            Map<Class<T>, Map<String, T>> classMap = new HashMap<Class<T>, Map<String, T>>();
            cache.set(classMap);
        }
        if ( !cache.get().containsKey(cl) ) {
            cache.get().put(cl, new HashMap<String, T>());
        }
        final Map<String, T> map = cache.get().get(cl);
        return map.get(id);
    }

    private static <T> void setCachedObject(Class<T> cl, String id, T object) {
        final ThreadLocal<Map<Class<T>, Map<String, T>>> cache = getCache(cl);
        if ( null == cache.get() ) {
            cache.set(new HashMap<Class<T>, Map<String, T>>());
        }
        if ( !cache.get().containsKey(cl) ) {
            cache.get().put(cl, new HashMap<String, T>());
        }
        cache.get().get(cl).put(id, object);
    }

    private static String getId(Object object) {
        Field idField = null;
        try {
            idField = ClassUtils.findFieldThroughClassHierarchy(object.getClass(), "id");
            if ( null == idField ) {
                // 细化
                return object.toString();
            }
            idField.setAccessible(true);
            return String.valueOf(idField.get(object));
        } catch (Exception ex) {
            return "";
        } finally {
            if ( null != idField ) idField.setAccessible(false);
        }
    }

    private static <T> T __createTransferObject(Class<T> toClazz, Object bizObject) throws InstantiationException,
            IllegalAccessException, Exception, InvocationTargetException {
        // New a blank tranfer object instance
        if ( null == bizObject ) {
            return null;
        }
        final String bizObjectId = getId(bizObject);
        final Object cachedObject = getCachedObject(toClazz, bizObjectId);
        if ( null != cachedObject ) {
            return (T) cachedObject;
        }
        final T result = toClazz.newInstance();
        setCachedObject(toClazz, bizObjectId, result);
        final Field[] targetFields = ClassUtils.getAllFields(toClazz);
        if ( null == targetFields || targetFields.length <= 0 ) {
            return result;
        }
        final Map<String, Field> sourceFieldMap = new HashMap<String, Field>();
        final Field[] sourceFields = ClassUtils.getAllFields(bizObject.getClass());
        if ( null == sourceFields || sourceFields.length <= 0 ) {
            return result;
        }
        // init source field into map for index
        for ( Field source : sourceFields ) {
            if ( !sourceFieldMap.containsKey(source.getName()) ) {
                sourceFieldMap.put(source.getName(), source);
            }
        }
        for ( Field target : targetFields ) {
            // ignore final field
            if ( Modifier.isFinal(target.getModifiers()) ) {
                continue;
            }
            if ( sourceFieldMap.containsKey(target.getName()) && null == target.getAnnotation(Binding.class) ) {
                Field source = sourceFieldMap.get(target.getName());
                boolean sourceAccessibleChanged = false;
                if ( !source.isAccessible() ) {
                    sourceAccessibleChanged = true;
                    source.setAccessible(true);
                }
                if ( target.isAccessible() ) {
                    target.set(result, source.get(bizObject));
                } else {
                    target.setAccessible(true);
                    target.set(result, source.get(bizObject));
                    target.setAccessible(false);
                }
                if ( sourceAccessibleChanged ) {
                    source.setAccessible(false);
                }
            } else {
                Binding binding = target.getAnnotation(Binding.class);
                if ( null == binding ) {
                    continue;
                }
                String navigation = binding.name();
                if ( null == navigation || navigation.trim().length() <= 0 ) {
                    navigation = target.getName();
                    // continue;
                }
                if ( null != navigation && navigation.trim().length() > 0 ) {
                    final String[] elements = navigation.split("\\.");
                    if ( null == elements || elements.length <= 0 ) {
                        continue;
                    }
                    List<String> dottedNameList = new LinkedList<String>();
                    for ( String element : elements ) {
                        dottedNameList.add(element);
                    }
                    assert dottedNameList.size() == elements.length;
                    Object value = null;
                    if ( AccessTypeEnum.Field == binding.accessType() ) {
                        value = getBindingValue(target.getType(), bizObject, dottedNameList);
                    } else if ( AccessTypeEnum.Property == binding.accessType() ) {
                        if ( dottedNameList.size() > 1 ) {
                            throw new UnsupportedOperationException("Property Based Access ONLY Support 1 level.");
                        }
                        final String propertyName;
                        if ( null != binding.name() && 0 < binding.name().trim().length() ) {
                            propertyName = binding.name();
                        } else {
                            propertyName = target.getName();
                        }
                        String getterName = "get" + propertyName.toUpperCase().charAt(0) + propertyName.substring(1);
                        Method getter = ClassUtils.findMethodThroughClassHierarchy(bizObject.getClass(), getterName);
                        value = getter.invoke(bizObject, null);
                    }
                    if ( BindingTypeEnum.Entity == binding.bindingType() ) {
                        Class<?> embeddedToClass = binding.embeddedType();
                        if ( target.getType().isAssignableFrom(Set.class) ) {
                            value = __assembleTransferObjectSet((Collection<?>) value, embeddedToClass);
                        } else if ( target.getType().isAssignableFrom(List.class) ) {
                            value = __assembleTransferObjectList((Collection<?>) value, embeddedToClass);
                        } else {
                            value = __createTransferObject(embeddedToClass, value);
                        }
                    }
                    if ( target.isAccessible() ) {
                        target.set(result, value);
                    } else {
                        target.setAccessible(true);
                        target.set(result, value);
                        target.setAccessible(false);
                    }
                }
            }
        }
        return result;
    }

    /**
     * get names[i] field from navigatingObject
     * 
     * @param <T>
     * @param clazz
     * @param navigatingObject
     * @param names
     * @return
     */
    @SuppressWarnings("unchecked")
    private static <T> T getBindingValue(Class<T> clazz, Object navigatingObject, List<String> names) throws Exception {
        if ( null == names || names.size() <= 0 ) {
            throw new IllegalStateException("navigating names are empty");
        }
        if ( null == navigatingObject ) {
            return null;
        }
        if ( names.size() == 1 ) {
            String name = names.remove(0);
            Field nextField = ClassUtils.findFieldThroughClassHierarchy(navigatingObject.getClass(), name);
            T result = null;
            if ( !nextField.isAccessible() ) {
                nextField.setAccessible(true);
                // if (nextField.getType().equals(clazz)) {
                result = (T) nextField.get(navigatingObject);
                // } else {
                // throw new IllegalStateException("The target field: name = "
                // + name + " is not instance of Type: "
                // + clazz.getName());
                // }
                nextField.setAccessible(false);
            } else {
                return (T) nextField.get(navigatingObject);
            }
            return result;
        } else {
            String name = names.remove(0);
            Field nextField = ClassUtils.findFieldThroughClassHierarchy(navigatingObject.getClass(), name);
            if ( null == nextField ) {
                throw new NoSuchFieldException("Field: " + name + " does not exist throught class hierarchy: " + navigatingObject.getClass());
            }
            T result = null;
            if ( !nextField.isAccessible() ) {
                nextField.setAccessible(true);
                result = getBindingValue(clazz, nextField.get(navigatingObject), names);
                nextField.setAccessible(false);
            } else {
                result = getBindingValue(clazz, nextField.get(navigatingObject), names);
            }
            return result;
        }
    }

    private TransferObjectFactory() {
    }
    // Question
}
