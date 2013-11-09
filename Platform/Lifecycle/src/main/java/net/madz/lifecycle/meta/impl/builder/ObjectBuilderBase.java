package net.madz.lifecycle.meta.impl.builder;

import java.lang.reflect.Method;
import java.util.ArrayList;

import net.madz.meta.MetaData;
import net.madz.verification.VerificationFailureSet;

public abstract class ObjectBuilderBase<SELF extends MetaData, PARENT extends MetaData> extends
        AnnotationMetaBuilderBase<SELF, PARENT> {

    protected interface MethodScanner {

        boolean onMethodFound(Method method, VerificationFailureSet failureSet);
    }

    public ObjectBuilderBase(PARENT parent, String name) {
        super(parent, name);
    }

    protected void scanMethodsOnClasses(Class<?>[] klasses, final VerificationFailureSet failureSet,
            final MethodScanner scanner) {
        if ( 0 == klasses.length ) return;
        final ArrayList<Class<?>> superclasses = new ArrayList<Class<?>>();
        for ( Class<?> klass : klasses ) {
            if ( klass == Object.class ) continue;
            for ( Method method : klass.getDeclaredMethods() ) {
                if ( scanner.onMethodFound(method, failureSet) ) {
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
        scanMethodsOnClasses(superclasses.toArray(new Class<?>[superclasses.size()]), failureSet, scanner);
    }
}