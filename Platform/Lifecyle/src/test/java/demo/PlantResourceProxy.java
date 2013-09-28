package demo;

public class PlantResourceProxy implements EntityProxy<PlantResource> , IPlant {

    private final PlantResource itself;

    public PlantResourceProxy(PlantResource proxy) {
        this.itself = proxy;
    }

    @Override
    public PlantResource get() {
        return itself;
    }

    public void addTask(ManualfactureTask itself2) {
        // TODO Auto-generated method stub
        
    }
}
