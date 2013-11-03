package net.madz.lifecycle.syntax;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ RegisterSyntaxNegativeTest.class, RegisterSyntaxPositiveTest.class, StateSetSyntaxPositiveTest.class,
        StateSetAndTransitionSetSyntaxNegativeTest.class, StateSyntaxNegativeTest.class, StateSyntaxPositiveTest.class,
        LMSyntaxTestSuite.class, RelationSyntaxNegativeTest.class, RelationSyntaxPositiveTest.class,
        ConditionSetTest.class, StateIndicatorNegativeTest.class, StateIndicatorPositiveTest.class })
public class SyntaxTestSuite {}
