package net.madz.util;

import java.lang.reflect.Method;
import java.util.ArrayList;



public abstract class MethodScannerImpl implements MethodScanCallback {

    public static void scanMethodsOnClasses(Class<?>[] klasses, final MethodScanCallback scanner) {
        if ( 0 == klasses.length ) return;
        final ArrayList<Class<?>> superclasses = new ArrayList<Class<?>>();
        for ( Class<?> klass : klasses ) {
            if ( klass == Object.class ) continue;
            for ( Method method : klass.getDeclaredMethods() ) {
                if ( scanner.onMethodFound(method) ) {
                    return;
                }
            }
            if ( null != klass.getSuperclass() && Object.class != klass ) {
                superclasses.add(klass.getSuperclass());
            }
            for ( Class<?> interfaze : klass.getInterfaces() ) {
                superclasses.add(interfaze);
            }
        }
        scanMethodsOnClasses(superclasses.toArray(new Class<?>[superclasses.size()]), scanner);
    }
    
}
