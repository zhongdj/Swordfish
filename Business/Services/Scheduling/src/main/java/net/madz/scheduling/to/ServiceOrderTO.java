package net.madz.scheduling.to;

import java.io.Serializable;
import java.util.List;

import net.madz.binding.annotation.Binding;

public class ServiceOrderTO implements Serializable{
    
    private static final long serialVersionUID = 8875587973768271476L;
    @Binding(name = "id")
    private Long serviceOrderId;
    
    @Binding(name = "plannedVolume")
    private Double plannedVolume;
    
    @Binding(name = "mixingPlantResource.id")
    private Long mixingPlantResourceId;

    @Binding(name = "mixingPlantResource.plannedVolume")
    private Double mixingPlantResourcePlannedVolume;
    
    @Binding(name = "mixingPlantResource.finishedVolume")
    private Double mixingPlantResourceFinishedVolume;
    
    @Binding(name = "mixingPlantResource.mixingPlant.id")
    private Long mixingPlantId;
    
    @Binding(name = "mixingPlantResource.mixingPlant.name")
    private String mixingPlantName;
    
    @Binding(name = "mixingPlantResource.mixingPlant.pinyinAbbrName")
    private String mixingPlantPinyinAbbrName;
    
    @Binding(name = "truckResource.id")
    private Long concreteTruckResourceId;
    
    @Binding(name = "truckResource.concreteTruck.id")
    private Long truckId;
    
    @Binding(name = "truckResource.concreteTruck.licencePlateNumber")
    private String truckLicencePlatNumber;
    
    @Binding(name = "truckResource.concreteTruck.ratedCapacity")
    private Double truckRatedCapacity;
    
    //@Binding(name = "spec.mixture.type")
    //private String mixtureType;
    
    @Binding(name = "spec.id")
    private Long pouringParSpectId;
    
    @Binding(name = "spec.pouringPart.id")
    private Long pouringPartId;
    
    @Binding(name = "spec.pouringPart.name")
    private String pouringPartName;
    
    @Binding(name = "spec.pouringPart.pinYinAbbrName")
    private String pouringPartAbbrName;
    
    @Binding(name = "spec.unitProject.id")
    private Long unitProjectId;
    
    @Binding(name = "spec.unitProject.name")
    private String unitProjectName;
    
    @Binding(name = "spec.mixture.id")
    private Long mixtureId;
    
    @Binding(name = "spec.mixture.gradeName")
    private String mixtureGradeName;
    
    @Binding(name = "spec.additives")
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

    
//    public String getMixtureType() {
//        return mixtureType;
//    }
//
//    
//    public void setMixtureType(String mixtureType) {
//        this.mixtureType = mixtureType;
//    }

    
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
