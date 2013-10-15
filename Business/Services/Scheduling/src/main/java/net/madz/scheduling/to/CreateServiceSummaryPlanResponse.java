package net.madz.scheduling.to;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class CreateServiceSummaryPlanResponse {

    @XmlPath("id/text()")
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
