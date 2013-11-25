package net.madz.core.biz;

import javax.ejb.EJBContext;
import javax.persistence.EntityManager;

import net.madz.core.entities.AbstractBaseEntity;

public class AbstractBO<T extends AbstractBaseEntity> implements IBizObject<T> {

    protected final T entity;
    protected EJBContext context;

    public AbstractBO(EntityManager em, Class<T> t, long id) {
        this(em.find(t, id));
    }

    public AbstractBO(T entity) {
        if ( null == entity ) {
            throw new RuntimeException("Entity must be not null when creating biz Object");
        }
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

    public void setEJBContext(EJBContext context) {
        this.context = context;
    }
}