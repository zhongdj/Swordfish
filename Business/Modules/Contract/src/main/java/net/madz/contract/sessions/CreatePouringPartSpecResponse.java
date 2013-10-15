package net.madz.contract.sessions;

import org.eclipse.persistence.oxm.annotations.XmlPath;


public class CreatePouringPartSpecResponse {
    @XmlPath("pouringPartSpecIds/")
    private PouringPartSpecInfo[] pouringPartSpecInfos;

    
    public PouringPartSpecInfo[] getPouringPartSpecInfos() {
        return pouringPartSpecInfos;
    }

    
    public void setPouringPartSpecInfos(PouringPartSpecInfo[] pouringPartSpecInfos) {
        this.pouringPartSpecInfos = pouringPartSpecInfos;
    }
    
    
}
