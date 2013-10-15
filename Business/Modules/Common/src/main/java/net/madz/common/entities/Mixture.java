package net.madz.common.entities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

@Entity
@Table(name = "mixture")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.CHAR, length = 1)
@NamedQuery(name = "Mixture.findByGradeName",
        query = "SELECT OBJECT(m) FROM Mixture AS m WHERE m.gradeName =:gradeName")
public abstract class Mixture extends CodedEntity {

    private static final long serialVersionUID = -5317402076349568390L;

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
