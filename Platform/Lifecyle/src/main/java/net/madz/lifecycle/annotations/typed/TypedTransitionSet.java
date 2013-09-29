package net.madz.lifecycle.annotations.typed;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.madz.lifecycle.ITransition;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TypedTransitionSet {

    Class<? extends ITransition> value();
}
