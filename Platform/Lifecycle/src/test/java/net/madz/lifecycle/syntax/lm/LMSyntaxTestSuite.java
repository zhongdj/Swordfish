package net.madz.lifecycle.syntax.lm;

import net.madz.lifecycle.syntax.lm.relation.LMSyntaxRelationPositiveTest;
import net.madz.lifecycle.syntax.lm.transition.TransitionNegativeTests;
import net.madz.lifecycle.syntax.lm.transition.TransitionPositiveTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ LMSyntaxNegativeTest.class, LMSyntaxPositiveTest.class, LMSyntaxRelationPositiveTest.class, TransitionPositiveTests.class,
        TransitionNegativeTests.class })
public class LMSyntaxTestSuite {}
