package net.madz.scheduling.entities;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.Transition;
import net.madz.scheduling.meta.PlantResourceLifecycleMeta;

import org.eclipse.persistence.annotations.Indexes;

@Entity
@Table(name = "mixing_plant_resource")
@Indexes()
@NamedQuery(name = "MixingPlantResource.findByPlantName", query = "SELECT OBJECT(m) FROM MixingPlantResource AS m WHERE m.mixingPlant.name = :mixingPlantName")
@LifecycleMeta(PlantResourceLifecycleMeta.class)
public class MixingPlantResource extends MixingPlantResourceBase {

    private static final long serialVersionUID = -3622084568259603724L;

    @Transition(PlantResourceLifecycleMeta.Transitions.Assign.class)
    public void assignOrder(ServiceOrder serviceOrder) {
        this.liveTasks.add(serviceOrder);
    }

    @Transition(PlantResourceLifecycleMeta.Transitions.Release.class)
    public void release() {
        this.liveTasks.remove(0);
    }

    @Transition(PlantResourceLifecycleMeta.Transitions.Maintain.class)
    void maintain() {}

    @Transition(PlantResourceLifecycleMeta.Transitions.ConfirmFixed.class)
    void confirmFixed() {}

    @Transition
    void detach() {}
}
