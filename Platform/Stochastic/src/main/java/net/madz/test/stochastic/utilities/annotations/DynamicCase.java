package net.madz.test.stochastic.utilities.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.madz.test.stochastic.core.IExploreStrategy;
import net.madz.test.stochastic.core.impl.explorers.ExhaustiveExploreStrategy;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE, ElementType.FIELD })
public @interface DynamicCase {

    int maxCombinations() default Integer.MAX_VALUE;

    // SampleSpace sampleSpace();
    Dimension[] dimensions();

    Filter[] filters() default {};

    Class<? extends IExploreStrategy> detector() default ExhaustiveExploreStrategy.class;

    Constraint[] constraints() default {};
}
