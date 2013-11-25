package net.madz.scheduling.entities;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import net.madz.authorization.entities.MultiTenancyEntity;
import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.StateIndicator;
import net.madz.lifecycle.annotations.Transition;
import net.madz.scheduling.biz.IPlantScheduleOrder;
import net.madz.scheduling.meta.PlantResourceLifecycleMeta;

import org.eclipse.persistence.annotations.Indexes;

@Entity
@Table(name = "mixing_plant_resource")
@Indexes()
@NamedQuery(name = "MixingPlantResource.findByPlantName", query = "SELECT OBJECT(m) FROM MixingPlantResource AS m WHERE m.mixingPlant.name = :mixingPlantName")
@LifecycleMeta(PlantResourceLifecycleMeta.class)
public class MixingPlantResource extends MultiTenancyEntity {

    private static final long serialVersionUID = -3622084568259603724L;
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumns({ @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false, referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "MIXING_PLANT_ID", nullable = false, insertable = true, updatable = false, referencedColumnName = "ID") })
    private MixingPlant mixingPlant;
    @Column(name = "FINISHED_VOLUME")
    private double finishedVolume;
    @Column(name = "PLANNED_VOLUME")
    private double plannedVolume;
    @OneToMany(targetEntity = ServiceOrder.class, mappedBy = "mixingPlantResource")
    private final List<IPlantScheduleOrder> liveTasks = new LinkedList<>();
    @Column(name = "STATE")
    @StateIndicator
    private String state = PlantResourceLifecycleMeta.States.Idle.class.getSimpleName();

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

    @Transition(PlantResourceLifecycleMeta.Transitions.Assign.class)
    public void assignOrder(ServiceOrder serviceOrder) {
        this.liveTasks.add(serviceOrder);
    }

    @Transition(PlantResourceLifecycleMeta.Transitions.Release.class)
    void release() {
        this.liveTasks.remove(0);
    }

    @Transition(PlantResourceLifecycleMeta.Transitions.Maintain.class)
    void maintain() {}

    @Transition(PlantResourceLifecycleMeta.Transitions.ConfirmFixed.class)
    void confirmFixed() {}

    @Transition
    void detach() {}
}
