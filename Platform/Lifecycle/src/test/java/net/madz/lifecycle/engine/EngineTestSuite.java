package net.madz.lifecycle.engine;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({ EngineCoreFunctionPositiveTests.class,EngineCoreFunctionNegativeTests.class })
public class EngineTestSuite {}
