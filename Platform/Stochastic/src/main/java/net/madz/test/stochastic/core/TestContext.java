package net.madz.test.stochastic.core;

import java.lang.reflect.Method;

import org.junit.runners.model.Statement;

public interface TestContext {

    Class<?> getTestClass();

    Method getTestMethod();
    
    Object getTarget();
    
    Statement getBase();
}
