package net.madz.test.stochastic.utilities.processors;

import net.madz.test.stochastic.core.AbsScriptEngine;
import net.madz.test.stochastic.core.GlobalTestContext;
import net.madz.test.stochastic.core.IFunctionRegistry;
import net.madz.test.stochastic.core.TestContext;
import net.madz.test.stochastic.utilities.annotations.FunctionRegistry;

public class FunctionRegistryProcessor extends AbsScriptEngine<FunctionRegistry> {

    @Override
    public void doProcess(TestContext context, FunctionRegistry t) {
        try {
            final Class<? extends IFunctionRegistry> value = t.value();
            IFunctionRegistry functionRegistry = value.newInstance();
            GlobalTestContext.getInstance().registerLocalFunctions(functionRegistry);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
