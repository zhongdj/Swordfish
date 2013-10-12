package net.madz.stochastic.demo.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.madz.stochastic.demo.processor.ActionProcessor;
import net.madz.test.stochastic.utilities.annotations.Processor;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Processor(ActionProcessor.class)
public @interface Action {

    String value();
}
