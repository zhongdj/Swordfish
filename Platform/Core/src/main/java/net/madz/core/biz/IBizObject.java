package net.madz.core.biz;

import javax.persistence.EntityManager;

import net.madz.lifecycle.IReactiveObject;

public interface IBizObject<T> {// extends IReactiveObject {

    Long getId();

    T get();

    void persist(EntityManager em);

    IBizObject<T> merge(EntityManager em);

    void remove(EntityManager em);
}
