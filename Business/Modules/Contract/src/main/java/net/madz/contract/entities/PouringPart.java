package net.madz.contract.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import net.madz.authorization.entities.MultiTenancyEntity;
import net.madz.core.annotations.PinYinIndex;
import net.madz.core.annotations.PinYinIndexed;

import org.eclipse.persistence.annotations.Index;

@Entity
@Table(name = "pouring_part")
@PinYinIndexed
@Index(name = "INDEX_POURING_PART_PINYIN_ABBR_NAME", columnNames = { "TENANT_ID", "PINYIN_ABBR_NAME" })
public class PouringPart extends MultiTenancyEntity {

    private static final long serialVersionUID = 6759494465303338186L;
    @Column(nullable = false, length = 20)
    private String name;
    @PinYinIndex(from = "name")
    @Column(name = "PINYIN_ABBR_NAME", length = 20)
    private String pinYinAbbrName;
    private String comment;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getPinYinAbbrName() {
        return pinYinAbbrName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
