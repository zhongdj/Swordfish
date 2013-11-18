package net.madz.lifecycle.syntax.basic;

import net.madz.lifecycle.syntax.lm.transition.TransitionTestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CompositeStateMachineNegativeTests.class, ConditionSetTest.class,
        StateSetAndTransitionSetSyntaxNegativeTest.class, StateSetSyntaxPositiveTest.class, TransitionTestSuite.class })
public class BasicTestSuite {}
