package net.madz.scheduling.to;

import net.madz.binding.annotation.Binding;

import org.eclipse.persistence.oxm.annotations.XmlPath;


public class MixingPlantResourceTO {
    @Binding(name = "id")
    @XmlPath("id/text()")
    private Long id;
    
    @Binding(name = "mixingPlant.name")
    @XmlPath("name/text()")
    private String name;
    
    @Binding(name = "mixingPlant.operator.fullName")
    @XmlPath("operatorName/text()")
    private String operatorName;

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public String getOperatorName() {
        return operatorName;
    }

    
    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }
    
}
