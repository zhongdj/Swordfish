package net.madz.contract.sessions;

import java.io.Serializable;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class AdditiveInfo implements Serializable {

    private static final long serialVersionUID = 5752630856796689993L;

    @XmlPath("id/text()")
    private long id;

    @XmlPath("name/text()")
    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
