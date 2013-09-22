package net.madz.contract.spec.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.madz.authorization.entities.StandardObject;
import net.madz.common.entities.Additive;
import net.madz.common.entities.Mixture;
import net.madz.contract.entities.PouringPart;
import net.madz.contract.entities.UnitProject;

@Entity
@Table(name = "pouring_part_spec")
public class PouringPartSpec extends StandardObject {

    private static final long serialVersionUID = 7944091197152689276L;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({ @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false, referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "POURING_PART_ID", nullable = false, insertable = true, updatable = true, referencedColumnName = "ID") })
    private PouringPart pouringPart;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({ @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false, referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "UNIT_PROJECT_ID", nullable = false, insertable = true, updatable = true, referencedColumnName = "ID") })
    private UnitProject unitProject;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({ @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false, referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "MIXTURE_ID", nullable = false, insertable = true, updatable = true, referencedColumnName = "ID") })
    private Mixture mixture;
    @ManyToMany
    @JoinTable(name = "pouring_part_spec_additive", joinColumns = {
            @JoinColumn(table = "pouring_part_spec", name = "TENANT_ID", updatable = true, insertable = true, referencedColumnName = "TENANT_ID"),
            @JoinColumn(table = "pouring_part_spec", name = "POURING_PART_SPEC_ID", updatable = true, insertable = true, referencedColumnName = "ID") },
            inverseJoinColumns = {
                    @JoinColumn(table = "additive", name = "TENANT_ID", updatable = false, insertable = false, referencedColumnName = "TENANT_ID"),
                    @JoinColumn(table = "additive", name = "ADDITIVE_ID", updatable = true, insertable = true, referencedColumnName = "ID") })
    private final List<Additive> additives = new ArrayList<>();

    public PouringPart getPouringPart() {
        return pouringPart;
    }

    public void setPouringPart(PouringPart pouringPart) {
        this.pouringPart = pouringPart;
    }

    public UnitProject getUnitProject() {
        return unitProject;
    }

    public void setUnitProject(UnitProject unitProject) {
        if ( null != this.unitProject ) {
            this.unitProject.remove(this);
        }
        this.unitProject = unitProject;
        this.unitProject.add(this);
    }

    public Mixture getMixture() {
        return mixture;
    }

    public void setMixture(Mixture mixture) {
        this.mixture = mixture;
    }

    public List<Additive> getAdditives() {
        return additives;
    }

    public void setAdditives(List<Additive> additives) {
        this.additives.clear();
        this.additives.addAll(additives);
    }
}
