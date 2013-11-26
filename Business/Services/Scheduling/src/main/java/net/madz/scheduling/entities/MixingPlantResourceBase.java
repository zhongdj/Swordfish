package net.madz.scheduling.entities;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import net.madz.authorization.entities.MultiTenancyEntity;
import net.madz.lifecycle.annotations.StateIndicator;
import net.madz.scheduling.biz.IPlantScheduleOrder;
import net.madz.scheduling.meta.PlantResourceLifecycleMeta;

@MappedSuperclass
public class MixingPlantResourceBase extends MultiTenancyEntity {

    private static final long serialVersionUID = 3449555012573257303L;
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumns({ @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false, referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "MIXING_PLANT_ID", nullable = false, insertable = true, updatable = false, referencedColumnName = "ID") })
    protected MixingPlant mixingPlant;
    @Column(name = "FINISHED_VOLUME")
    protected double finishedVolume;
    @Column(name = "PLANNED_VOLUME")
    protected double plannedVolume;
    @OneToMany(targetEntity = ServiceOrder.class, mappedBy = "mixingPlantResource")
    protected final List<IPlantScheduleOrder> liveTasks = new LinkedList<>();
    @Column(name = "STATE")
    @StateIndicator
    private String state = PlantResourceLifecycleMeta.States.Idle.class.getSimpleName();

    public MixingPlantResourceBase() {
        super();
    }

    public String getState() {
        return state;
    }

    public MixingPlant getMixingPlant() {
        return mixingPlant;
    }

    public void setMixingPlant(MixingPlant mixingPlant) {
        this.mixingPlant = mixingPlant;
    }

    public List<IPlantScheduleOrder> getLiveTasks() {
        return liveTasks;
    }

    public void setLiveTasks(List<ServiceOrder> liveTasks) {
        this.liveTasks.clear();
        this.liveTasks.addAll(liveTasks);
    }

    public double getFinishedVolume() {
        return finishedVolume;
    }

    public void setFinishedVolume(double finishedVolume) {
        this.finishedVolume = finishedVolume;
    }

    public double getPlannedVolume() {
        return plannedVolume;
    }

    public void setPlannedVolume(double plannedVolume) {
        this.plannedVolume = plannedVolume;
    }
}