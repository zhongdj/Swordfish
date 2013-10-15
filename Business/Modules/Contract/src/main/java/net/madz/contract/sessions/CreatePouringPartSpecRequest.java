package net.madz.contract.sessions;

import java.io.Serializable;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class CreatePouringPartSpecRequest implements Serializable{

    private static final long serialVersionUID = -718769026754329778L;

    @XmlPath("part-info/name/text()")
    private String pouringPartName;

    @XmlPath("part-info/comment/text()")
    private String pouringPartComment;

    @XmlPath("spec-info/mixtureId/text()")
    private long mixtureId;

    @XmlPath("spec-info/mixtureGradeName/text()")
    private String mixtureGradeName;

    @XmlPath("spec-info/additives/")
    private AdditiveInfo[] additives;

    @XmlPath("coded-spec/text()")
    private String codedSpec;

    public String getPouringPartName() {
        return pouringPartName;
    }

    public void setPouringPartName(String pouringPartName) {
        this.pouringPartName = pouringPartName;
    }

    public String getPouringPartComment() {
        return pouringPartComment;
    }

    public void setPouringPartComment(String pouringPartComment) {
        this.pouringPartComment = pouringPartComment;
    }

    public long getMixtureId() {
        return mixtureId;
    }

    public void setMixtureId(long mixtureId) {
        this.mixtureId = mixtureId;
    }

    public String getMixtureGradeName() {
        return mixtureGradeName;
    }

    public void setMixtureGradeName(String mixtureGradeName) {
        this.mixtureGradeName = mixtureGradeName;
    }

    public AdditiveInfo[] getAdditives() {
        return additives;
    }

    public void setAdditives(AdditiveInfo[] additives) {
        this.additives = additives;
    }

    public String getCodedSpec() {
        return codedSpec;
    }

    public void setCodedSpec(String codedSpec) {
        this.codedSpec = codedSpec;
    }
}
