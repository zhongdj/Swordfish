package net.madz.test.stochastic.core.impl;

import java.util.ArrayList;
import java.util.List;

import net.madz.test.stochastic.core.IDimension;
import net.madz.test.stochastic.core.IDynamicCase;
import net.madz.test.stochastic.core.IExploreStrategy;
import net.madz.test.stochastic.core.IGlobalDimension;
import net.madz.test.stochastic.core.TestContext;
import net.madz.test.stochastic.utilities.annotations.Constraint;
import net.madz.test.stochastic.utilities.annotations.Dimension;
import net.madz.test.stochastic.utilities.annotations.DynamicCase;
import net.madz.test.stochastic.utilities.annotations.Filter;

final class GlobalDimensionBasedDynamicCase implements IDynamicCase {

    private final DynamicCase testCase;

    GlobalDimensionBasedDynamicCase(DynamicCase testCase) {
        this.testCase = testCase;
    }

    @Override
    public int maxCombinations() {
        return testCase.maxCombinations();
    }

    @Override
    public Dimension[] dimensions() {
        return testCase.dimensions();
    }

    @Override
    public Filter[] filters() {
        return testCase.filters();
    }

    @Override
    public Class<? extends IExploreStrategy> detector() {
        return testCase.detector();
    }

    @Override
    public Constraint[] constraints() {
        return testCase.constraints();
    }

    @Override
    public List<IDimension> getDimensions(final TestContext context) {
        final List<IDimension> dimensions = new ArrayList<IDimension>();
        final Dimension[] definedDimensions = testCase.dimensions();
        // Loading Dimension in Order
        for ( Dimension dimension : definedDimensions ) {
            if ( IGlobalDimension.class.isAssignableFrom(dimension.dimensionClass()) ) {
                dimensions.add(newDimension(dimension));
            }
        }
        return dimensions;
    }

    private IDimension newDimension(Dimension dimension) {
        try {
            final IDimension iDimension = dimension.dimensionClass().newInstance();
            iDimension.setAlias(dimension.alias());
            iDimension.setEnumType(dimension.enumClass());
            iDimension.setPriority(dimension.priority());
            return iDimension;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}