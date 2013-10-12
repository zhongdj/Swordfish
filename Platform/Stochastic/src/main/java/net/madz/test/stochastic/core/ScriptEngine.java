package net.madz.test.stochastic.core;

import java.lang.annotation.Annotation;

public interface ScriptEngine<META> {

    public abstract void doProcess(TestContext context, META t);

    public abstract void doProcess(TestContext context, META[] ts);

    public void executeScript(TestContext context, final Annotation annotation);

    public void processAnnotation(TestContext context, final Annotation[] annotations);
}