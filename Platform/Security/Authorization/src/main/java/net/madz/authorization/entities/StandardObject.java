/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.madz.authorization.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.madz.core.entities.AbstractBaseEntity;

/**
 * 
 * @author Barry
 */
@MappedSuperclass
public abstract class StandardObject extends AbstractBaseEntity {

    private static final long serialVersionUID = -5489441885227863280L;
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumns(value = { @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false, referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "CREATED_BY", nullable = false, insertable = true, updatable = false, referencedColumnName = "ID") })
    protected User createdBy;
    @Column(name = "CREATED_ON")
    @Temporal(value = TemporalType.TIMESTAMP)
    protected Date createdOn;
    @Column(name = "DELETED")
    protected Boolean deleted = false;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns(value = { @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false, referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "UPDATED_BY", nullable = false, insertable = true, updatable = false, referencedColumnName = "ID") })
    protected User updatedBy;
    @Column(name = "UPDATED_ON")
    @Temporal(value = TemporalType.TIMESTAMP)
    protected Date updatedOn;

    public User getCreatedBy() {
        return createdBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }
}
