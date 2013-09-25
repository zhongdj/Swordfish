package net.madz.contract.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.madz.authorization.entities.StandardObject;
import net.madz.customer.entities.CustomerAccount;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

@Entity
@Table(name = "contract")
public class Contract extends StandardObject {

    private static final long serialVersionUID = 6888298311566859312L;
    @ManyToOne
    @JoinColumns({ @JoinColumn(name = "TENANT_ID", insertable = false, updatable = false, referencedColumnName = "TENANT_ID", nullable = false),
            @JoinColumn(name = "CUSTOMER_ID", insertable = true, updatable = true, nullable = false, referencedColumnName = "ID") })
    private CustomerAccount customer;
    private String name;
    @Column(name = "START_DATE")
    @Temporal(TemporalType.DATE)
    private Date startDate;
    @Column(name = "END_DATE")
    @Temporal(TemporalType.DATE)
    private Date endDate;
    @OneToMany(mappedBy = "contract", fetch = FetchType.LAZY)
    @XmlInverseReference(mappedBy="contract")
    private final List<UnitProject> unitProjects = new ArrayList<>();

    public CustomerAccount getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerAccount customer) {
        this.customer = customer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<UnitProject> getUnitProjects() {
        return unitProjects;
    }

    public void setUnitProjects(List<UnitProject> unitProjects) {
        if ( null != this.unitProjects ) {
            this.unitProjects.clear();
        }
        this.unitProjects.addAll(unitProjects);
    }

    public void remove(UnitProject unitProject) {
        this.unitProjects.remove(unitProject);
    }

    public void add(UnitProject unitProject) {
        this.unitProjects.add(unitProject);
    }
}
