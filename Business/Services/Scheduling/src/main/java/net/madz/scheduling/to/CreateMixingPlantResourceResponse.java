package net.madz.scheduling.to;

import net.madz.binding.annotation.Binding;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class CreateMixingPlantResourceResponse {

    @Binding(name = "id")
    @XmlPath("id/text()")
    private Long id;

    @Binding(name = "mixingPlant.id")
    @XmlPath("mixingPlantId/text()")
    private Long mixingPlantId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMixingPlantId() {
        return mixingPlantId;
    }

    public void setMixingPlantId(Long mixingPlantId) {
        this.mixingPlantId = mixingPlantId;
    }
}
