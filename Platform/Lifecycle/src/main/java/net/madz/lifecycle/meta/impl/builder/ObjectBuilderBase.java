package net.madz.lifecycle.meta.impl.builder;

import java.lang.reflect.Method;
import java.util.ArrayList;

import net.madz.lifecycle.meta.MetaObject;
import net.madz.lifecycle.meta.MetaType;
import net.madz.lifecycle.meta.builder.AnnotationMetaBuilder;
import net.madz.meta.MetaData;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailureSet;

public abstract class ObjectBuilderBase<SELF extends MetaObject<SELF, TYPE>, PARENT extends MetaData, TYPE extends MetaType<TYPE>> extends
        InheritableAnnotationMetaBuilderBase<SELF, PARENT> implements MetaObject<SELF, TYPE> {

    private TYPE metaType;

    protected interface MethodScanner {

        boolean onMethodFound(Method method, VerificationFailureSet failureSet);
    }

    public ObjectBuilderBase(PARENT parent, String name) {
        super(parent, name);
    }

    @Override
    public boolean hasSuper() {
        return false;
    }

    @Override
    protected SELF findSuper(Class<?> metaClass) throws VerificationException {
        return null;
    }

    @Override
    public TYPE getMetaType() {
        return metaType;
    }

    protected void setMetaType(TYPE metaType) {
        this.metaType = metaType;
    }

    @Override
    public AnnotationMetaBuilder<SELF, PARENT> build(Class<?> klass, PARENT parent) throws VerificationException {
        setPrimaryKey(getMetaType().getPrimaryKey());
        addKeys(getMetaType().getKeySet());
        return this;
    }

    public static void scanMethodsOnClasses(Class<?>[] klasses, final VerificationFailureSet failureSet, final MethodScanner scanner) {
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