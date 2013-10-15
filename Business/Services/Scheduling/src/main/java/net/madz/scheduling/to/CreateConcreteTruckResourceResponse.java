package net.madz.scheduling.to;

import net.madz.binding.annotation.Binding;

public class CreateConcreteTruckResourceResponse {

    private long id;

    @Binding(name = "concreteTruck.id")
    private long concreteTruckId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getConcreteTruckId() {
        return concreteTruckId;
    }

    public void setConcreteTruckId(long concreteTruckId) {
        this.concreteTruckId = concreteTruckId;
    }
}
