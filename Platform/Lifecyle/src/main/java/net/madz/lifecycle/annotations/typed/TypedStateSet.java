package net.madz.lifecycle.annotations.typed;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.madz.lifecycle.IReactiveObject;
import net.madz.lifecycle.IState;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TypedStateSet {

    Class<? extends IState<? extends IReactiveObject, ?>> value();

}
