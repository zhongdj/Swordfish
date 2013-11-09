package net.madz.lifecycle;

import net.madz.lifecycle.engine.EngineTestSuite;
import net.madz.lifecycle.semantics.SemanticsTestSuite;
import net.madz.lifecycle.syntax.SyntaxTestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ SyntaxTestSuite.class, SemanticsTestSuite.class, EngineTestSuite.class })
public class AllTestSuites {}
