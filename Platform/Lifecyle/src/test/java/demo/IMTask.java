package demo;

public interface IMTask<T extends ITruck, P extends IPlant> {

    public abstract void allocate(T r1, P r2);
}