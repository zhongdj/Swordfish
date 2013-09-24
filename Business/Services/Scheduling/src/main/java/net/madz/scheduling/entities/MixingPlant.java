package net.madz.scheduling.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import net.madz.authorization.entities.StandardObject;
import net.madz.core.annotations.PinYinIndex;
import net.madz.core.annotations.PinYinIndexed;

@Entity
@Table(name = "mixing_plant")
@PinYinIndexed
public class MixingPlant extends StandardObject {

    private static final long serialVersionUID = 1113872983257589610L;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(name = "PINYIN_ABBR_NAME", nullable = false, length = 20)
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
