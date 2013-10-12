package net.madz.test.stochastic.core;

import java.util.Map;

import net.madz.test.stochastic.utilities.IFunction;

public interface IFunctionRegistry {

    void onLoadFuctions(Map<String, IFunction> localFunctionsMap);
}
