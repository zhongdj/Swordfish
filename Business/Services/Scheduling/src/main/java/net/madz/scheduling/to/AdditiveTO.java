package net.madz.scheduling.to;

import net.madz.binding.annotation.Binding;

public class AdditiveTO {

    @Binding(name = "id")
    private Long addtiveId;
    @Binding(name = "name")
    private String additiveName;
    @Binding(name = "pinyinAbbrName")
    private String additivePinyinAbbrName;
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
