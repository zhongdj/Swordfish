package net.madz.lifecycle.meta;

import net.madz.meta.MetaData;

public interface Instance<T> extends MetaData {

    T getTemplate();
}
