package demo;

public class TruckProxy implements EntityProxy<TruckResource>, ITruck {

    private final TruckResource itself;

    public TruckProxy(TruckResource itself) {
        super();
        this.itself = itself;
    }

    @Override
    public TruckResource get() {
        return this.itself;
    }

    @Override
    public void addTask(ManualfactureTask task) {
        // requireWorkable();
        try {
            // doSomething;
            // setState(newState);
        } finally {
        }
    }
}
