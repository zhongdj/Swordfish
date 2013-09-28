package net.madz.lifecycle.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Transition {
    public static String NULL = "NULL";

    String value() default NULL;
}
