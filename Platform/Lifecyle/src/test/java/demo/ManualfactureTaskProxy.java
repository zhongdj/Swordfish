package demo;

public class ManualfactureTaskProxy implements EntityProxy<ManualfactureTask>, 
 IMTask<TruckProxy, PlantResourceProxy> {

    private final ManualfactureTask itself;

    public ManualfactureTaskProxy(ManualfactureTask itself) {
        super();
        this.itself = itself;
    }

    @Override
    public ManualfactureTask get() {
        return this.itself;
    }

    @Override
    public void allocate(TruckProxy r1, PlantResourceProxy r2) {
        this.itself.allocate(r1.get(), r2.get());
        r1.addTask(this.itself);
        r2.addTask(this.itself);
    }
}
