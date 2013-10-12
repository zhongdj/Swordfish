package net.madz.test.stochastic.core;

public interface IExpectation {

    boolean isNegative();

    String getFormalizedString();

    /**
     * Should throw RuntimeException when fail to verify
     * 
     * @param context
     */
    void verify(DynamicCaseContext context);
}
