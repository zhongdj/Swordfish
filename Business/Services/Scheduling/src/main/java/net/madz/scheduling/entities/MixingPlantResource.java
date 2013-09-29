package net.madz.scheduling.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import net.madz.authorization.entities.StandardObject;

import org.eclipse.persistence.annotations.Indexes;

@Entity
@Table(name = "mixing_plant_resource")
@Indexes()
public class MixingPlantResource extends StandardObject {

    private static final long serialVersionUID = -3622084568259603724L;

    @OneToOne
    @JoinColumns({
            @JoinColumn(name = "TENANT_ID", nullable = false, insertable = false, updatable = false,
                    referencedColumnName = "TENANT_ID"),
            @JoinColumn(name = "MIXING_PLANT_ID", nullable = false, insertable = true, updatable = false,
                    referencedColumnName = "ID") })
    private MixingPlant mixingPlant;

    @Column(name = "FINISHED_VOLUME")
    private double finishedVolume;

    @Column(name = "PLANNED_VOLUME")
    private double plannedVolume;

    @OneToMany(mappedBy = "mixingPlantResource")
    private final List<ServiceOrder> liveTasks = new ArrayList<>();

    public MixingPlant getMixingPlant() {
        return mixingPlant;
    }

    public void setMixingPlant(MixingPlant mixingPlant) {
        this.mixingPlant = mixingPlant;
    }

    public List<ServiceOrder> getLiveTasks() {
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
