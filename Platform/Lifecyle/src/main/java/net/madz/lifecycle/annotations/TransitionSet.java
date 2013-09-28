package net.madz.lifecycle.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import net.madz.lifecycle.ITransition;

@Retention(RetentionPolicy.RUNTIME)
public @interface TransitionSet {

    Class<? extends ITransition> value();

}
