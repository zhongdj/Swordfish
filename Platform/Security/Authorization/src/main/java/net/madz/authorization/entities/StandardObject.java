/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.madz.authorization.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.madz.core.entities.MultiTenancyEntity;

/**
 * 
 * @author a
 */
@MappedSuperclass
public class StandardObject extends MultiTenancyEntity implements Serializable {

    private static final long serialVersionUID = -5489441885227863280L;
    @JoinColumn(name = "CREATED_BY", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    protected User createdBy;
    @Column(name = "CREATED_ON")
    @Temporal(value = TemporalType.TIMESTAMP)
    protected Date createdOn;
    @Column(name = "DELETED")
    protected Boolean deleted = false;
    @JoinColumn(name = "UPDATED_BY", referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.EAGER)
    protected User updatedBy;
    @Column(name = "UPDATED_ON")
    @Temporal(value = TemporalType.TIMESTAMP)
    protected Date updatedOn;

    public StandardObject() {
    }

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
