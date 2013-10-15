/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.madz.authorization.entities;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.DiscriminatorType;
import javax.persistence.MappedSuperclass;

import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.MultitenantType;
import org.eclipse.persistence.annotations.TenantDiscriminatorColumn;

/**
 * 
 * @author a
 */
@MappedSuperclass
@Access(AccessType.FIELD)
@Multitenant(MultitenantType.SINGLE_TABLE)
@TenantDiscriminatorColumn(name = "TENANT_ID", contextProperty = "tenant.id", discriminatorType = DiscriminatorType.INTEGER, primaryKey = true)
public class MultiTenancyEntity extends StandardObject {

    private static final long serialVersionUID = -2866660841222176750L;
}
