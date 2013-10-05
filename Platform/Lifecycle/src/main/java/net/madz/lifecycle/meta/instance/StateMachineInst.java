package net.madz.lifecycle.meta.instance;

import java.lang.reflect.Method;

import net.madz.lifecycle.meta.Instance;
import net.madz.lifecycle.meta.template.StateMachineMetadata;

public interface StateMachineInst extends Instance<StateMachineMetadata> {

    TransitionInst[] getTransitionSet();

    boolean hasTransition(Object transitionKey);

    TransitionInst getTransition(Object transitionKey);

    StateInst[] getStateSet();
    
    StateInst getState(Object stateKey);
    
    Method stateGetter();

    Method stateSetter();
}
