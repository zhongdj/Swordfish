package net.madz.scheduling.to;

public class CreateMixingPlantResourceRequest {

    private String mixingPlantName;

    private String operatorName;

    public String getMixingPlantName() {
        return mixingPlantName;
    }

    public void setMixingPlantName(String mixingPlantName) {
        this.mixingPlantName = mixingPlantName;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }
}
