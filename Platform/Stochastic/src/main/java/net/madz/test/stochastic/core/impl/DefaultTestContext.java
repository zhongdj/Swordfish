package net.madz.test.stochastic.core.impl;

import java.lang.reflect.Method;

import org.junit.runners.model.Statement;

import net.madz.test.stochastic.core.TestContext;

public final class DefaultTestContext implements TestContext {

    public Object getTarget() {
        return target;
    }

    private final Method testMethod;

    private final Class<?> testClass;

    private final Object target;

    private final Statement base;

    public DefaultTestContext(Statement base, Class<?> testClass, Method m, Object target) {
        this.base = base;
        this.testClass = testClass;
        this.testMethod = m;
        this.target = target;
    }

    @Override
    public Method getTestMethod() {
        return testMethod;
    }

    @Override
    public Class<?> getTestClass() {
        return testClass;
    }

    @Override
    public Statement getBase() {
        return base;
    }
}