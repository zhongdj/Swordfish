package net.madz.test.stochastic.utilities.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.madz.test.stochastic.core.DefaultGlobalDimension;
import net.madz.test.stochastic.core.IDimension;
import net.madz.test.stochastic.core.IDomain;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE, ElementType.FIELD })
public @interface Dimension {

    String alias();

    Class<? extends Enum<?>> enumClass();

    @SuppressWarnings("rawtypes")
    Class<? extends IDomain> domainClass() default IDomain.class;

    Class<? extends IDimension> dimensionClass() default DefaultGlobalDimension.class;

    int priority() default 10;
}
