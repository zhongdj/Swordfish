package net.madz.contract.sessions;

import java.io.Serializable;

public class CreateContractResponse implements Serializable {

    private static final long serialVersionUID = -6261877540032880044L;

    private Long customerId;

    private Long contractId;

    private Long[] unitProjectIds;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long[] getUnitProjectIds() {
        return unitProjectIds;
    }

    public void setUnitProjectIds(Long[] unitProjectIds) {
        this.unitProjectIds = unitProjectIds;
    }
}
