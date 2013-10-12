package net.madz.test.stochastic.core;

import net.madz.test.stochastic.core.impl.dimensions.DefaultDimension;

public class DefaultGlobalDimension extends DefaultDimension implements IGlobalDimension {

    @Override
    public String getDottedName() {
        return getAlias();
    }

}
