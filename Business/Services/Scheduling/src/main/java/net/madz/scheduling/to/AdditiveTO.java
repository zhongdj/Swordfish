package net.madz.scheduling.to;

import org.eclipse.persistence.oxm.annotations.XmlPath;

import net.madz.binding.annotation.Binding;

public class AdditiveTO {

    @XmlPath("id/text()")
    @Binding(name = "id")
    private Long addtiveId;

    @XmlPath("name/text()")
    @Binding(name = "name")
    private String additiveName;

    @XmlPath("pinyinAbbrName/text()")
    @Binding(name = "pinyinAbbrName")
    private String additivePinyinAbbrName;

    @XmlPath("code/text()")
    @Binding(name = "code")
    private String additiveCode;

    public Long getAddtiveId() {
        return addtiveId;
    }

    public void setAddtiveId(Long addtiveId) {
        this.addtiveId = addtiveId;
    }

    public String getAdditiveName() {
        return additiveName;
    }

    public void setAdditiveName(String additiveName) {
        this.additiveName = additiveName;
    }

    public String getAdditivePinyinAbbrName() {
        return additivePinyinAbbrName;
    }

    public void setAdditivePinyinAbbrName(String additivePinyinAbbrName) {
        this.additivePinyinAbbrName = additivePinyinAbbrName;
    }

    public String getAdditiveCode() {
        return additiveCode;
    }

    public void setAdditiveCode(String additiveCode) {
        this.additiveCode = additiveCode;
    }
}
