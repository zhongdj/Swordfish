package demo;

import java.util.ArrayList;
import java.util.List;

public class PlantResource implements IPlant {

    private String state;

    private List<ManualfactureTask> list = new ArrayList<>();

    public void addToQueue(ManualfactureTask task) {
        list.add(task);
    }

    public void addTask(ManualfactureTask manualfactureTask) {
    }
}
