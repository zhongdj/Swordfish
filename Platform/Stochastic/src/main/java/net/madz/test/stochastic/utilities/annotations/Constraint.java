package net.madz.test.stochastic.utilities.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE, ElementType.FIELD })
public @interface Constraint {

    public static enum ConstraintTypeEnum {
        Inclusion,
        Exclusion
    }

    ConstraintTypeEnum type() default ConstraintTypeEnum.Inclusion;

    ConstraintDimension[] combination();
}
