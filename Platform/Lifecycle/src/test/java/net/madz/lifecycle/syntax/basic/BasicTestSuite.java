package net.madz.lifecycle.syntax.basic;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CompositeStateMachineNegativeTests.class, ConditionSetTest.class, StateSetAndTransitionSetSyntaxNegativeTest.class,
        StateSetSyntaxPositiveTest.class })
public class BasicTestSuite {}
