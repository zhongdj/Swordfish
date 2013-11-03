package net.madz.lifecycle.annotations.relation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.madz.lifecycle.annotations.Null;

@Target({ ElementType.PARAMETER , ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Relation {

    Class<?> value() default Null.class;
}
