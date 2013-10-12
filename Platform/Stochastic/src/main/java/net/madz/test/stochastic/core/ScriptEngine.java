package net.madz.test.stochastic.core;

import java.lang.annotation.Annotation;

public interface ScriptEngine<META> {

    public void executeScript(final TestContext context, final Annotation annotation) throws Throwable;

    public void processAnnotation(final TestContext context, final Annotation[] annotations) throws Throwable;
}