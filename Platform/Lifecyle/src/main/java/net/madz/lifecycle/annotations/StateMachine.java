package net.madz.lifecycle.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface StateMachine {

    StateSet states();

    TransitionSet transitions();

}
