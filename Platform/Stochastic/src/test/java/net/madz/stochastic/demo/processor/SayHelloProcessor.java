package net.madz.stochastic.demo.processor;

import net.madz.stochastic.demo.annotations.SayHello;
import net.madz.test.stochastic.core.AbsScriptEngine;
import net.madz.test.stochastic.core.TestContext;

public class SayHelloProcessor extends AbsScriptEngine<SayHello> {

    @Override
    public void doProcess(TestContext context, SayHello t) {
        System.out.println("Hello, " + t.value());
    }
}
