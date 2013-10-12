package net.madz.test.stochastic.core;

public interface IPairDimension extends IDimension {

    void setOne(String oneExpression);

    String getOneExpression();

    String getOtherExpression();

    void setOther(String otherExpression);

    Object getOne();

    Object getOther();
}
