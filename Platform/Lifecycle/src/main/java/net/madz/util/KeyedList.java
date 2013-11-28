package net.madz.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.madz.lifecycle.meta.MultiKeyed;

public class KeyedList<T extends MultiKeyed> {

    private final ArrayList<T> elements = new ArrayList<>();
    private final HashMap<Object, T> elementMap = new HashMap<>();
    private final Class<T> tClass;

    public KeyedList(Class<T> tClass) {
        if ( null == tClass ) {
            throw new IllegalArgumentException("tClass cannot be Null");
        }
        this.tClass = tClass;
    }

    public void add(T element) {
        this.elements.add(element);
        final Iterator<Object> iterator = element.getKeySet().iterator();
        while ( iterator.hasNext() ) {
            this.elementMap.put(iterator.next(), element);
        }
    }

    public T get(Object key) {
        return this.elementMap.get(key);
    }

    @SuppressWarnings("unchecked")
    public T[] toArray() {
        return this.elements.toArray((T[]) Array.newInstance(tClass, this.elements.size()));
    }

    public boolean containsKey(Object transitionKey) {
        return null != get(transitionKey);
    }
}
