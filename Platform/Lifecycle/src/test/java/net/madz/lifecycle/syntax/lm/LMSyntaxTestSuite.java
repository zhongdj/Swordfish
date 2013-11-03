package net.madz.lifecycle.syntax.lm;

import net.madz.lifecycle.syntax.lm.relation.LMSyntaxRelationPositiveTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({ LMSyntaxNegativeTest.class, LMSyntaxPositiveTest.class , LMSyntaxRelationPositiveTest.class})
public class LMSyntaxTestSuite {}
