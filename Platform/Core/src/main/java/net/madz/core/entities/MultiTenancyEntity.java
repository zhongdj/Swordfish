/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.madz.core.entities;

import javax.persistence.DiscriminatorType;
import javax.persistence.MappedSuperclass;
import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.MultitenantType;
import org.eclipse.persistence.annotations.TenantDiscriminatorColumn;

/**
 * 
 * @author Barry
 */
@MappedSuperclass
@Multitenant(MultitenantType.SINGLE_TABLE)
@TenantDiscriminatorColumn(name = "TENANT_ID", contextProperty = "tenant.id", discriminatorType = DiscriminatorType.INTEGER, primaryKey = true)
public abstract class MultiTenancyEntity extends AbstractBaseEntity {
}
