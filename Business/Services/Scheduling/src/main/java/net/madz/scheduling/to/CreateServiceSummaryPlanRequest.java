package net.madz.scheduling.to;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class CreateServiceSummaryPlanRequest {

    @XmlPath("specId/text()")
    private long specId;

    @XmlPath("totalVolume/text()")
    private double totalVolume;

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
