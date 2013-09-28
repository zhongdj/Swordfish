package net.madz.lifecycle;

import java.util.Map;
import java.util.Set;

/**
 * A state is a condition or situation in the life of an reactive object during
 * which it satisfies some condition.
 * 
 * A state performs some activity.
 * 
 * A state waits for some event.
 * 
 * For more information please refer to:
 * 
 * http://staruml.sourceforge.net/docs/user-guide(en)/ch05_5.html
 * 
 * @author barry
 * 
 * @param <R>
 * @param <SELF>
 */
@SuppressWarnings("rawtypes")
public interface IState<R extends IReactiveObject, SELF extends IState> {

    Map<? extends ITransition, SELF> getTransitionFunction();


    Set<? extends ITransition> getOutboundTransitions();
}
