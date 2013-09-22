package net.madz.common.entities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;


@Entity
@Table(name="mixture")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.CHAR, length = 1)
public abstract class Mixture extends CodedEntity {

    @Column(name = "GRADE_NAME", nullable = false)
    protected String gradeName;

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    @PrePersist
    @PreUpdate
    private void convertNameToCode() {
        String result = gradeName.replaceAll("\\.", "");
        setCode(result + "----");
    }
}
