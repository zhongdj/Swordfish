package net.madz.lifecycle.engine;

import net.madz.lifecycle.engine.callback.CallbackTestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ EngineCoreFunctionPositiveTests.class, EngineCoreFunctionNegativeTests.class, EngineCoreCompositeStateMachinePositiveTests.class,
        EngineCoreCompositeStateMachineNegativeTests.class, CallbackTestSuite.class, LifecycleLockTests.class, LifecycleEventTests.class, ReturnTypeTests.class,
        StateSetterTests.class, MultipleStateMachineTests.class })
public class EngineTestSuite {}
