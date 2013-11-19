package net.madz.lifecycle.meta;

import net.madz.meta.MetaData;

public interface MetaObject<O extends MetaObject<O, T>, T extends MetaType<T>> extends MetaData {

    T getMetaType();
    
    public static interface ReadAccessor<T> {

        T read(Object reactiveObject);
    }
}
