package net.madz.core.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.madz.core.biz.BOFactory;
import net.madz.core.biz.IBizObject;
import net.madz.core.entities.AbstractBaseEntity;

public class ProxyList<E extends AbstractBaseEntity, T extends IBizObject<E>> implements List<T> {

    private final List<E> entityList;
    private final Class<T> tClass;

    public ProxyList(Class<T> tClass, List<E> entityList) {
        this.entityList = entityList;
        this.tClass = tClass;
    }

    @Override
    public int size() {
        return this.entityList.size();
    }

    @Override
    public boolean isEmpty() {
        return this.entityList.isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
        if ( o instanceof IBizObject ) {
            IBizObject<E> bo = (IBizObject<E>) o;
            return this.entityList.contains(bo.get());
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return new ProxyIterator<E, T>(tClass, this.entityList.iterator());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object[] toArray() {
        final Object[] wrappedArray = this.entityList.toArray();
        final Object[] result = new Object[wrappedArray.length];
        for ( int i = 0; i < wrappedArray.length; i++ ) {
            result[i] = BOFactory.create(tClass, (E) wrappedArray[i]);
        }
        return result;
    }

    @SuppressWarnings({ "unchecked", "hiding" })
    @Override
    public <T> T[] toArray(T[] a) {
        final Object[] wrappedArray = this.entityList.toArray();
        final Object[] result = new Object[wrappedArray.length];
        for ( int i = 0; i < wrappedArray.length; i++ ) {
            result[i] = BOFactory.create(tClass, (E) wrappedArray[i]);
        }
        return (T[]) result;
    }

    @Override
    public boolean add(T e) {
        return this.entityList.add(e.get());
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object o) {
        if ( o instanceof IBizObject ) {
            return this.entityList.remove(( (IBizObject<E>) o ).get());
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean containsAll(Collection<?> c) {
        for ( Object object : c ) {
            if ( !( c instanceof IBizObject ) ) {
                return false;
            } else {
                T bo = (T) object;
                if ( !this.entityList.contains(bo.get()) ) {
                    return false;
                }
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean addAll(Collection<? extends T> c) {
        for ( Object object : c ) {
            if ( !( c instanceof IBizObject ) ) {
                return false;
            } else {
                T bo = (T) object;
                if ( !this.entityList.contains(bo.get()) ) {
                    this.entityList.add(bo.get());
                }
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        int position = index;
        for ( Object object : c ) {
            if ( !( c instanceof IBizObject ) ) {
                return false;
            } else {
                T bo = (T) object;
                this.entityList.add(position, bo.get());
            }
            position++;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean removeAll(Collection<?> c) {
        for ( Object object : c ) {
            if ( !( c instanceof IBizObject ) ) {
                return false;
            } else {
                T bo = (T) object;
                this.entityList.remove(bo.get());
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean retainAll(Collection<?> c) {
        final List<E> entities = this.entityList;
        for ( E item : entities ) {
            boolean matched = false;
            for ( Object object : c ) {
                if ( !( object instanceof IBizObject ) ) {
                    return false;
                } else {
                    T bo = (T) object;
                    if ( item.equals(bo.get()) ) {
                        matched = true;
                    }
                }
            }
            if ( !matched ) {
                entities.remove(item);
            }
        }
        return true;
    }

    @Override
    public void clear() {
        this.entityList.clear();
    }

    @Override
    public T get(int index) {
        E e = this.entityList.get(index);
        return BOFactory.create(tClass, e);
    }

    @Override
    public T set(int index, T element) {
        this.entityList.set(index, element.get());
        return element;
    }

    @Override
    public void add(int index, T element) {
        this.entityList.add(index, element.get());
    }

    @Override
    public T remove(int index) {
        E remove = this.entityList.remove(index);
        return BOFactory.create(this.tClass, remove);
    }

    @SuppressWarnings("unchecked")
    @Override
    public int indexOf(Object o) {
        if ( !( o instanceof IBizObject ) ) {
            return -1;
        } else {
            T bo = (T) o;
            return this.entityList.indexOf(bo.get());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public int lastIndexOf(Object o) {
        if ( !( o instanceof IBizObject ) ) {
            return -1;
        } else {
            T bo = (T) o;
            return this.entityList.lastIndexOf(bo.get());
        }
    }

    @Override
    public ListIterator<T> listIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unused")
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        List<T> result = new ArrayList<T>();
        List<E> subList = this.entityList.subList(fromIndex, toIndex);
        return BOFactory.create(tClass, subList);
    }
}
