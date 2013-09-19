/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.madz.core.entities;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * 
 * @author Barry
 */
@MappedSuperclass
public abstract class AbstractBaseEntity {

    @Id
    @GeneratedValue
    protected long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
