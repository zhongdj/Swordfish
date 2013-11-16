package net.madz.lifecycle.annotations.callback;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreStateChange {

    public static final String NULL_STR = "$null$";

    Class<?> from() default AnyState.class;

    Class<?> to() default AnyState.class;

    String relation() default NULL_STR;

    String mappedBy() default NULL_STR;
}
