package net.madz.core.biz;

import javax.persistence.EntityManager;

import net.madz.core.entities.AbstractBaseEntity;

public class AbstractBO<T extends AbstractBaseEntity> implements IBizObject<T> {

    protected final T entity;

    public AbstractBO(EntityManager em, Class<T> t, long id) {
        this(em.find(t, id));
    }

    public AbstractBO(T entity) {
        this.entity = entity;
    }

    @Override
    public Long getId() {
        return ( null == entity || 0 >= entity.getId() ) ? null : entity.getId();
    }

    @Override
    public T get() {
        return entity;
    }

    @Override
    public void persist(EntityManager em) {
        em.persist(entity);
    }

    @Override
    public AbstractBO<T> merge(EntityManager em) {
        return new AbstractBO<T>(em.merge(entity));
    }

    @Override
    public void remove(EntityManager em) {
        em.remove(entity);
    }
}