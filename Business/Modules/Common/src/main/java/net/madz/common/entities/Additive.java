package net.madz.common.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import net.madz.core.annotations.PinYinIndex;
import net.madz.core.annotations.PinYinIndexed;

import org.eclipse.persistence.annotations.Index;

@Entity
@Table(name = "additive")
@PinYinIndexed
public class Additive extends CodedEntity {

    private static final long serialVersionUID = 2488317593704593687L;

    @Column(nullable = false, length = 10)
    private String name;

    @Index(name = "INDEX_ADDITIVE_PINYIN_ABBR_NAME")
    @PinYinIndex(from = "name")
    @Column(name = "PINYIN_ABBR_NAME", nullable = false, length = 10)
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

    public void setAdditiveEnum(AdditiveEnum additive) {
    }

    public static enum AdditiveEnum {
        WinterBuildingConstructionAboveNegtive10("------1"),
        WinterBuildingConstructionBelowNegtive10("------2"),
        SuperFluid("------C"),
        Pavement("------L"),
        FineAggregate("------X"),
        AntiFreezingNegtive5("-----1-"),
        AntiFreezingNegtive10("-----2-"),
        AntiFreezingNegtive15("-----3-"),
        AntiFreezingNegtive20("-----4-"),
        WaterTightS10("----0--"),
        WaterTightS12("----2--"),
        WaterTightS6("----6--"),
        WaterTightS8("----8--"),
        MicroExpansion("----W--"),
        SuperEarlyStrength("---C---"),
        Retarder("---H---"),
        EarlyStrength("---Z---");

        private String code;

        private AdditiveEnum(String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }
    }
}
