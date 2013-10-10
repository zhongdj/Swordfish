package net.madz.common.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

import net.madz.core.annotations.PinYinIndex;
import net.madz.core.annotations.PinYinIndexed;
import net.madz.core.entities.AbstractBaseEntity;

import org.eclipse.persistence.annotations.Index;

@Entity
@Table(name = "construction_category")
@PinYinIndexed
// @TableGenerator(name = "CONSTRUCTION_CATEGORY_SEQUENCE", initialValue = 1,
// allocationSize = 1)
public class ConstructionCategory extends AbstractBaseEntity {

    private static final long serialVersionUID = -7058590808090558306L;

    private String name;

    @Index(name = "INDEX_CONSTRUCTION_CATEGORY_PINYIN_ABBR_NAME")
    @PinYinIndex(from = "name")
    private String pinyinAbbrName;

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
}
