package net.madz.test.stochastic.core;

import java.util.List;

import net.madz.test.stochastic.utilities.annotations.Constraint;
import net.madz.test.stochastic.utilities.annotations.Dimension;
import net.madz.test.stochastic.utilities.annotations.Filter;

//TODO [Barry][Code Review] [Use interfaces instead of annotation.]
public interface IDynamicCase {

    int maxCombinations();

    Dimension[] dimensions();

    Filter[] filters();

    Class<? extends IExploreStrategy> detector();

    Constraint[] constraints();

    List<IDimension> getDimensions(final TestContext context);
}
