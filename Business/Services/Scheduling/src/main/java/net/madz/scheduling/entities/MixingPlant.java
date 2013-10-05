package net.madz.scheduling.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import net.madz.authorization.entities.MultiTenancyEntity;
import net.madz.authorization.entities.User;
import net.madz.core.annotations.PinYinIndex;
import net.madz.core.annotations.PinYinIndexed;

@Entity
@Table(name = "mixing_plant")
@PinYinIndexed
public class MixingPlant extends MultiTenancyEntity {

    private static final long serialVersionUID = 1113872983257589610L;
    @Column(nullable = false, length = 20)
    private String name;
    @Column(name = "PINYIN_ABBR_NAME", nullable = false, length = 20)
    @PinYinIndex(from = "name")
    private String pinyinAbbrName;
    @OneToOne
    @JoinColumns(value = { @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false, referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "OPERATOR_ID", nullable = false, insertable = true, updatable = false, referencedColumnName = "ID") })
    private User operator;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinyinAbbrName() {
        return pinyinAbbrName;
    }

    public void setPinyinAbbrName(String pinyinAbbrName) {
        this.pinyinAbbrName = pinyinAbbrName;
    }

    public User getOperator() {
        return operator;
    }

    public void setOperator(User operator) {
        this.operator = operator;
    }
}
