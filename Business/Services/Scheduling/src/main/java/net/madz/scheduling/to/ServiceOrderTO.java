package net.madz.scheduling.to;

import java.io.Serializable;
import java.util.List;


public class ServiceOrderTO implements Serializable{
    
    private static final long serialVersionUID = 8875587973768271476L;
    
    private Long serviceOrderId;
    
    private Double plannedVolume;
    
    private Long mixingPlantResourceId;
    
    private Double mixingPlantResourcePlannedVolume;
    
    private Double mixingPlantResourceFinishedVolume;
    
    private Long mixingPlantId;
    
    private String mixingPlantName;
    
    private String mixingPlantPinyinAbbrName;
    
    private Long concreteTruckResourceId;
    
    private Long truckId;
    
    private String truckLicencePlatNumber;
    
    private Double truckRatedCapacity;
    
    private String mixtureType;
    
    private Long pouringParSpectId;
    
    private Long pouringPartId;
    
    private String pouringPartName;
    
    private String pouringPartAbbrName;
    
    private Long unitProjectId;
    
    private String unitProjectName;
    
    private Long mixtureId;
    
    private String mixtureGradeName;
    
    private List<AdditiveTO> additives;

    
    public Long getServiceOrderId() {
        return serviceOrderId;
    }

    
    public void setServiceOrderId(Long serviceOrderId) {
        this.serviceOrderId = serviceOrderId;
    }

    
    public Double getPlannedVolume() {
        return plannedVolume;
    }

    
    public void setPlannedVolume(Double plannedVolume) {
        this.plannedVolume = plannedVolume;
    }

    
    public Long getMixingPlantResourceId() {
        return mixingPlantResourceId;
    }

    
    public void setMixingPlantResourceId(Long mixingPlantResourceId) {
        this.mixingPlantResourceId = mixingPlantResourceId;
    }

    
    public Double getMixingPlantResourcePlannedVolume() {
        return mixingPlantResourcePlannedVolume;
    }

    
    public void setMixingPlantResourcePlannedVolume(Double mixingPlantResourcePlannedVolume) {
        this.mixingPlantResourcePlannedVolume = mixingPlantResourcePlannedVolume;
    }

    
    public Double getMixingPlantResourceFinishedVolume() {
        return mixingPlantResourceFinishedVolume;
    }

    
    public void setMixingPlantResourceFinishedVolume(Double mixingPlantResourceFinishedVolume) {
        this.mixingPlantResourceFinishedVolume = mixingPlantResourceFinishedVolume;
    }

    
    public Long getMixingPlantId() {
        return mixingPlantId;
    }

    
    public void setMixingPlantId(Long mixingPlantId) {
        this.mixingPlantId = mixingPlantId;
    }

    
    public String getMixingPlantName() {
        return mixingPlantName;
    }

    
    public void setMixingPlantName(String mixingPlantName) {
        this.mixingPlantName = mixingPlantName;
    }

    
    public String getMixingPlantPinyinAbbrName() {
        return mixingPlantPinyinAbbrName;
    }

    
    public void setMixingPlantPinyinAbbrName(String mixingPlantPinyinAbbrName) {
        this.mixingPlantPinyinAbbrName = mixingPlantPinyinAbbrName;
    }

    
    public Long getConcreteTruckResourceId() {
        return concreteTruckResourceId;
    }

    
    public void setConcreteTruckResourceId(Long concreteTruckResourceId) {
        this.concreteTruckResourceId = concreteTruckResourceId;
    }

    
    public Long getTruckId() {
        return truckId;
    }

    
    public void setTruckId(Long truckId) {
        this.truckId = truckId;
    }

    
    public String getTruckLicencePlatNumber() {
        return truckLicencePlatNumber;
    }

    
    public void setTruckLicencePlatNumber(String truckLicencePlatNumber) {
        this.truckLicencePlatNumber = truckLicencePlatNumber;
    }

    
    public Double getTruckRatedCapacity() {
        return truckRatedCapacity;
    }

    
    public void setTruckRatedCapacity(Double truckRatedCapacity) {
        this.truckRatedCapacity = truckRatedCapacity;
    }

    
    public String getMixtureType() {
        return mixtureType;
    }

    
    public void setMixtureType(String mixtureType) {
        this.mixtureType = mixtureType;
    }

    
    public Long getPouringParSpectId() {
        return pouringParSpectId;
    }

    
    public void setPouringParSpectId(Long pouringParSpectId) {
        this.pouringParSpectId = pouringParSpectId;
    }

    
    public Long getPouringPartId() {
        return pouringPartId;
    }

    
    public void setPouringPartId(Long pouringPartId) {
        this.pouringPartId = pouringPartId;
    }

    
    public String getPouringPartName() {
        return pouringPartName;
    }

    
    public void setPouringPartName(String pouringPartName) {
        this.pouringPartName = pouringPartName;
    }

    
    public String getPouringPartAbbrName() {
        return pouringPartAbbrName;
    }

    
    public void setPouringPartAbbrName(String pouringPartAbbrName) {
        this.pouringPartAbbrName = pouringPartAbbrName;
    }

    
    public Long getUnitProjectId() {
        return unitProjectId;
    }

    
    public void setUnitProjectId(Long unitProjectId) {
        this.unitProjectId = unitProjectId;
    }

    
    public String getUnitProjectName() {
        return unitProjectName;
    }

    
    public void setUnitProjectName(String unitProjectName) {
        this.unitProjectName = unitProjectName;
    }

    
    public Long getMixtureId() {
        return mixtureId;
    }

    
    public void setMixtureId(Long mixtureId) {
        this.mixtureId = mixtureId;
    }

    
    public String getMixtureGradeName() {
        return mixtureGradeName;
    }

    
    public void setMixtureGradeName(String mixtureGradeName) {
        this.mixtureGradeName = mixtureGradeName;
    }

    
    public List<AdditiveTO> getAdditives() {
        return additives;
    }

    
    public void setAdditives(List<AdditiveTO> additives) {
        this.additives = additives;
    }
    
}
