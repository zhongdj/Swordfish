package net.madz.lifecycle.annotations.callback;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.madz.lifecycle.annotations.Null;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreStateChange {

    public static final String NULL_STR = "$null$";

    Class<?> from() default AnyState.class;

    Class<?> to() default AnyState.class;

    String observableName() default NULL_STR;

    Class<?> observableClass() default Null.class;

    String mappedBy() default NULL_STR;
}
