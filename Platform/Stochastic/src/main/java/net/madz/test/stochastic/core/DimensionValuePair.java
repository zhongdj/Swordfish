package net.madz.test.stochastic.core;

public class DimensionValuePair {

    private final IDimension dimension;
    private final String choice;

    public DimensionValuePair(IDimension dimension, String choice) {
        super();
        this.dimension = dimension;
        this.choice = choice;
    }

    public IDimension getDimension() {
        return dimension;
    }

    public String getChoice() {
        return choice;
    }
}
