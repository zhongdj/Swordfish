package demo;

public class ManualfactureTask implements IMTask<TruckResource, PlantResource> {

    private String state;

    private TruckResource r1;

    private PlantResource r2;

    /*
     * (non-Javadoc)
     * 
     * @see demo.IMTask#allocate(demo.TruckResource, demo.PlantResource)
     */
    @Override
    public void allocate(TruckResource r1, PlantResource r2) {
        this.r1 = r1;
        this.r2 = r2;
    }
}
