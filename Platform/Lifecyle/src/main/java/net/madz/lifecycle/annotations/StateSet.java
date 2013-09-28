package net.madz.lifecycle.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import net.madz.lifecycle.IReactiveObject;
import net.madz.lifecycle.IState;

@Retention(RetentionPolicy.RUNTIME)
public @interface StateSet {

    Class<? extends IState<? extends IReactiveObject, ?>> value();

}
