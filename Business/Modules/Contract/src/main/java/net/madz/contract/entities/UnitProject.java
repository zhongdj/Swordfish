package net.madz.contract.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import net.madz.authorization.entities.MultiTenancyEntity;
import net.madz.common.entities.Address;
import net.madz.contract.spec.entities.PouringPartSpec;
import net.madz.customer.entities.Contact;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

@Entity
@Table(name = "unit_project")
public class UnitProject extends MultiTenancyEntity {

    private static final long serialVersionUID = 1067321008139265973L;
    @Column(nullable = false, length = 40)
    private String name;
    private Address address;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({ @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false, referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "CONTACT_ID", nullable = false, insertable = true, updatable = true, referencedColumnName = "ID") })
    private Contact contact;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({ @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false, referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "CONTRACT_ID", nullable = false, insertable = true, updatable = true, referencedColumnName = "ID") })
    private Contract contract;
    @OneToMany(mappedBy = "unitProject", fetch = FetchType.LAZY)
    @XmlInverseReference(mappedBy="unitProject")
    private final List<PouringPartSpec> pouringPartSpecs = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        if ( null != this.contract ) {
            this.contract.remove(this);
        }
        this.contract = contract;
        this.contract.add(this);
    }

    public List<PouringPartSpec> getPouringPartSpecs() {
        return pouringPartSpecs;
    }

    public void setPouringPartSpecs(List<PouringPartSpec> pouringPartSpecs) {
        this.pouringPartSpecs.clear();
        this.pouringPartSpecs.addAll(pouringPartSpecs);
    }

    public void remove(PouringPartSpec pouringPartSpec) {
        this.pouringPartSpecs.remove(pouringPartSpec);
    }

    public void add(PouringPartSpec pouringPartSpec) {
        if ( this.pouringPartSpecs.contains(pouringPartSpec) ) {
            return;
        }
        this.pouringPartSpecs.add(pouringPartSpec);
    }
}
