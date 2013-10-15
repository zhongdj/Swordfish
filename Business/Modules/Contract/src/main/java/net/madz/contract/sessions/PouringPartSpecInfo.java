package net.madz.contract.sessions;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class PouringPartSpecInfo {

    @XmlPath("id/text()")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
