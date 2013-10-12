package net.madz.test.stochastic.core;

public interface IDimension {

    void setAlias(String alias);

    @SuppressWarnings("rawtypes")
    void setEnumType(Class<? extends Enum> enumClass);

    @SuppressWarnings("rawtypes")
    Class<? extends Enum> getEnumType();

    /**
     * Only for generated report.
     * 
     * @return
     */
    String getAlias();

    void choose(TestContext context, String choice) throws Throwable;

    String[] values();

    void setPriority(int priority);

    int getPriority();

    String getDottedName();
}