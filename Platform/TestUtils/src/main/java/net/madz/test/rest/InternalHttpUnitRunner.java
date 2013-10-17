package net.madz.test.rest;

import java.util.List;

import net.madz.test.stochastic.core.TestContext;

import org.junit.runners.model.InitializationError;

public class InternalHttpUnitRunner extends HttpUnitRunner {

    private TestContext externalContext;

    public InternalHttpUnitRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    public InternalHttpUnitRunner(TestContext context, Class<?> klass) throws InitializationError {
        super(klass);
        this.externalContext = context;
    }

    @Override
    protected Object createTest() throws Exception {
        return getTestClass().getJavaClass().newInstance();
    }

    @Override
    protected void collectInitializationErrors(List<Throwable> errors) {
        validateNoNonStaticInnerClass(errors);
        validateInstanceMethods(errors);
        validateFields(errors);
    }

}
