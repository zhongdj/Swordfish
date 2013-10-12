package net.madz.stochastic.demo.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.madz.stochastic.demo.processor.SayHelloProcessor;
import net.madz.test.stochastic.utilities.annotations.Processor;

@Target(ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
@Processor(SayHelloProcessor.class)
public @interface SayHello {
    String value();
}
