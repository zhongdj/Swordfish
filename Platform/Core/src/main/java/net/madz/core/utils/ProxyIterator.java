package net.madz.core.utils;

import java.util.Iterator;

import net.madz.core.biz.BOFactory;
import net.madz.core.biz.IBizObject;
import net.madz.core.entities.AbstractBaseEntity;

public class ProxyIterator<E extends AbstractBaseEntity, T extends IBizObject<E>> implements Iterator<T> {

    private Iterator<E> wrappedIterator;
    private Class<T> tClass;

    public ProxyIterator(Class<T> tClass, Iterator<E> iterator) {
        this.wrappedIterator = iterator;
        this.tClass = tClass;
    }

    @Override
    public boolean hasNext() {
        return wrappedIterator.hasNext();
    }

    @Override
    public T next() {
        return BOFactory.create(tClass, wrappedIterator.next());
    }

    @Override
    public void remove() {
        wrappedIterator.remove();
    }
}
