package net.madz.lifecycle.syntax;

import net.madz.lifecycle.syntax.basic.BasicTestSuite;
import net.madz.lifecycle.syntax.basic.transition.TransitionSyntaxTestSuite;
import net.madz.lifecycle.syntax.lm.LMSyntaxTestSuite;
import net.madz.lifecycle.syntax.lm.stateindicator.StateIndicatorNegativeTest;
import net.madz.lifecycle.syntax.lm.stateindicator.StateIndicatorPositiveTest;
import net.madz.lifecycle.syntax.register.RegisterSyntaxNegativeTest;
import net.madz.lifecycle.syntax.register.RegisterSyntaxPositiveTest;
import net.madz.lifecycle.syntax.relation.RelationSyntaxNegativeTest;
import net.madz.lifecycle.syntax.relation.RelationSyntaxPositiveTest;
import net.madz.lifecycle.syntax.state.StateSyntaxNegativeTest;
import net.madz.lifecycle.syntax.state.StateSyntaxPositiveTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ RegisterSyntaxNegativeTest.class, RegisterSyntaxPositiveTest.class, BasicTestSuite.class, StateSyntaxNegativeTest.class,
        StateSyntaxPositiveTest.class, TransitionSyntaxTestSuite.class, LMSyntaxTestSuite.class, RelationSyntaxNegativeTest.class,
        RelationSyntaxPositiveTest.class, StateIndicatorNegativeTest.class, StateIndicatorPositiveTest.class })
public class SyntaxTestSuite {}
