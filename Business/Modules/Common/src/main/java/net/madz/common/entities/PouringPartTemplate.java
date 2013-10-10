package net.madz.common.entities;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.madz.core.entities.AbstractBaseEntity;

@Entity
@Table(name = "pouring_part_template")
public class PouringPartTemplate extends AbstractBaseEntity {

    private static final long serialVersionUID = -3053022546016915999L;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CONSTRUCTION_CATEGORY_ID")
    private ConstructionCategory category;

    private String name;

    private int prirority;

    public ConstructionCategory getCategory() {
        return category;
    }

    public void setCategory(ConstructionCategory category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrirority() {
        return prirority;
    }

    public void setPrirority(int prirority) {
        this.prirority = prirority;
    }
}
