package net.madz.test.stochastic.core;

public interface IConstraint {

    public static String EXCLUSION_PLACEHOLDER = "-";
    public static String INCLUSION_PLACEHOLDER = "*";

    public static enum ConstraintType {
        Inclusion,
        Exclusion,
        Combination
    }

    ConstraintType getConstraintType();

    boolean skip(int depth, String[] choices);
}
