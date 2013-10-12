package net.madz.test.stochastic.utilities.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.madz.test.stochastic.core.AbsScriptEngine;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface Processor {

    Class<? extends AbsScriptEngine<?>> value();

    String[] processSequence() default {};
}
