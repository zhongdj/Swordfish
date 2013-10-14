package net.madz.scheduling.to;

import net.madz.binding.annotation.Binding;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class ServiceSummaryPlanTO {

    @XmlPath("id/text()")
    private long id;

    @Binding(name = "spec.id")
    @XmlPath("specId/text()")
    private long specId;

    @XmlPath("totalVolume/text()")
    private double totalVolume;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSpecId() {
        return specId;
    }

    public void setSpecId(long specId) {
        this.specId = specId;
    }

    public double getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(double totalVolume) {
        this.totalVolume = totalVolume;
    }
}
