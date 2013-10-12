package net.madz.test.stochastic.core;

import java.util.List;
import java.util.Map;

public interface IExploreStrategy {

    void setCombinationHandler(ICombinationHandler handler);

    // void doExplore(List<IDimension> dimensions, IFilter[] filters);
    void doExplore(List<IDimension> dimensions, IFilter[] filters, IConstraint constraint, Map<String, Integer> dimensionIndex);
}
