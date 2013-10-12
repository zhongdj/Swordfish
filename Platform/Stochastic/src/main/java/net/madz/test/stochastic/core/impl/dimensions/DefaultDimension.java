package net.madz.test.stochastic.core.impl.dimensions;

import net.madz.test.stochastic.core.IDimension;

public abstract class DefaultDimension extends AbstractDimension implements IDimension {

    @SuppressWarnings("rawtypes")
    protected Class<? extends Enum> enumClass;
    protected String alias;
    protected int priority;

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public DefaultDimension() {
        super();
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class<? extends Enum> getEnumType() {
        return enumClass;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void setEnumType(Class<? extends Enum> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public void setAlias(String alias) {
        this.alias = alias;
    }
}