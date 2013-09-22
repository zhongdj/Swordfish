package net.madz.core.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import net.madz.core.annotations.PinYinIndex.PinYinType;
import net.madz.core.annotations.PinYinIndexed.PinYinIndexedProcessor;
import net.madz.core.utils.PinyinUtils;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendEntityAnnotationProcessor(value = PinYinIndexedProcessor.class, callbackAt = {
		PrePersist.class, PreUpdate.class })
public @interface PinYinIndexed {
    
    public static class PinYinIndexedProcessor implements EntityAnnotationProcessor<PinYinIndexed> {

        @Override
        public void processAnnotation(Object entity, PinYinIndexed a) {
            final ArrayList<Field> fields = new ArrayList<>();
            for ( Class<?> c = entity.getClass(); !c.equals(Object.class); c = c.getSuperclass() ) {
                for ( Field field : c.getDeclaredFields() ) {
                    if ( null == field.getAnnotation(PinYinIndex.class) ) {
                        continue;
                    }
                    fields.add(field);
                }
            }
            Collections.reverse(fields);
            for ( final Field field : fields ) {
                final PinYinIndex annotation = field.getAnnotation(PinYinIndex.class);
                final Field fromField = findFromClassHierarchy(entity.getClass(), annotation.from());
                if ( null == fromField ) {
                    Logger.getLogger(getClass().getName()).info("Cannot find @PinYinIndex.from(\"" + annotation.from() + "\") on Field: " + field.getName());
                } else {
                    process(entity, fromField, field, annotation);
                }
            }
        }

        private void process(Object entity, Field fromField, Field targetField, PinYinIndex annotation) {
            boolean accessibilityChanged = false;
            try {
                if ( !fromField.isAccessible() ) {
                    accessibilityChanged = true;
                    fromField.setAccessible(true);
                }
                final String chineseString = fromField.get(entity).toString();
                final String targetValue;
                if ( PinYinType.Abbr == annotation.type() ) {
                    targetValue = PinyinUtils.getPinYinHeadChar(chineseString);
                } else {
                    targetValue = PinyinUtils.getPinYin(chineseString);
                }
                setValue(entity, targetField, targetValue);
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            } finally {
                if ( accessibilityChanged ) {
                    fromField.setAccessible(false);
                }
            }
        }

        private void setValue(Object entity, Field targetField, String targetValue) {
            // if setter found, use setter, otherwise use field
            if ( null == entity || null == targetField || 0 >= targetField.getName().length() ) {
                throw new IllegalStateException();
            }
            final Method[] declaredMethods = entity.getClass().getDeclaredMethods();
            final String fieldName = targetField.getName();
            String expectedMethodName = null;
            if ( fieldName.length() > 1 ) {
                expectedMethodName = "set" + fieldName.substring(0).toUpperCase() + fieldName.substring(1, fieldName.length() - 1);
            } else {
                expectedMethodName = "set" + fieldName.substring(0).toUpperCase();
            }
            Method expectedMethod = null;
            for ( Method method : declaredMethods ) {
                if ( method.getName().equals(expectedMethodName) ) {
                    expectedMethod = method;
                }
            }
            if ( null != expectedMethod ) {
                boolean accessibilityChanged = false;
                try {
                    if ( !expectedMethod.isAccessible() ) {
                        expectedMethod.setAccessible(true);
                        accessibilityChanged = true;
                    }
                    expectedMethod.invoke(entity, targetValue);
                } catch (Exception e) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                } finally {
                    if ( accessibilityChanged ) {
                        expectedMethod.setAccessible(false);
                    }
                }
            } else {
                boolean accessibilityChanged = false;
                if ( !targetField.isAccessible() ) {
                    targetField.setAccessible(true);
                    accessibilityChanged = true;
                }
                try {
                    targetField.set(entity, targetValue);
                } catch (Exception e) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                } finally {
                    if ( accessibilityChanged ) {
                        targetField.setAccessible(false);
                    }
                }
            }
        }

        @SuppressWarnings("rawtypes")
        public Method findFromClassHierarchy(Class from, String methodName, Class[] paramClasses) {
            for ( Class<?> c = from; !c.equals(Object.class); c = c.getSuperclass() ) {
                try {
                    Method method = c.getDeclaredMethod(methodName, paramClasses);
                    if ( null != method ) {
                        return method;
                    }
                } catch (Exception e) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                }
            }
            return null;
        }

        @SuppressWarnings("rawtypes")
        public Field findFromClassHierarchy(Class from, String fieldName) {
            for ( Class<?> c = from; !c.equals(Object.class); c = c.getSuperclass() ) {
                try {
                    Field field = c.getDeclaredField(fieldName);
                    if ( null != field ) {
                        return field;
                    }
                } catch (Exception e) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                }
            }
            return null;
        }

        @SuppressWarnings("rawtypes")
        public List<Field> findFieldsFromClassHierarchyAnnotatedWith(Class from, Class<? extends Annotation> annotation) {
            final ArrayList<Field> fields = new ArrayList<>();
            for ( Class<?> c = from; !c.equals(Object.class); c = c.getSuperclass() ) {
                for ( Field field : c.getDeclaredFields() ) {
                    if ( null == field.getAnnotation(annotation) ) {
                        continue;
                    }
                    fields.add(field);
                }
            }
            Collections.reverse(fields);
            return fields;
        }

        @SuppressWarnings("rawtypes")
        public List<Method> findMethodFromClassHierarchyAnnotatedWith(Class from, Class<? extends Annotation> annotation) {
            final ArrayList<Method> methods = new ArrayList<>();
            for ( Class<?> c = from; !c.equals(Object.class); c = c.getSuperclass() ) {
                for ( Method method : c.getDeclaredMethods() ) {
                    if ( null == method.getAnnotation(annotation) ) {
                        continue;
                    }
                    methods.add(method);
                }
            }
            Collections.reverse(methods);
            return methods;
        }
    }
}
