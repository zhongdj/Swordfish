package net.madz.core.biz;

import javax.persistence.EntityManager;

public interface IBizObject<T> {

    Long getId();

    T get();

    void persist(EntityManager em);

    IBizObject<T> merge(EntityManager em);

    void remove(EntityManager em);
}
