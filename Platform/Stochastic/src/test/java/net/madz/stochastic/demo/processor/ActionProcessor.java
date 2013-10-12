package net.madz.stochastic.demo.processor;

import net.madz.stochastic.demo.annotations.Action;
import net.madz.test.stochastic.core.AbsScriptEngine;
import net.madz.test.stochastic.core.TestContext;


public class ActionProcessor extends AbsScriptEngine<Action> {

    @Override
    public void doProcess(TestContext context, Action t) {
        System.out.println("Do action: " + t.value());
    }
}
