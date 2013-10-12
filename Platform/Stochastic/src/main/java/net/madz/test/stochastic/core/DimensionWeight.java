package net.madz.test.stochastic.core;

public class DimensionWeight implements Comparable<DimensionWeight> {

    private final String dottedName;
    private int count;

    public DimensionWeight(final String alias, final int count) {
        this.dottedName = alias;
        this.count = count;
    }

    public void increment() {
        count++;
    }

    @Override
    public int compareTo(DimensionWeight o) {
        return o.count - count;
    }

    public String getDottedName() {
        return dottedName;
    }
}