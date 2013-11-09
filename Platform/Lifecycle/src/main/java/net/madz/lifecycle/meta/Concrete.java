package net.madz.lifecycle.meta;

import net.madz.meta.MetaData;

public interface Concrete<T> extends MetaData {

    T getTemplate();
}
