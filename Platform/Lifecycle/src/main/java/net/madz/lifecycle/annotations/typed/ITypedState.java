package net.madz.lifecycle.annotations.typed;

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
public interface ITypedState<R extends ITypedReactiveObject, SELF extends ITypedState> {

    Map<? extends ITypedTransition, SELF> getTransitionFunction();


    Set<? extends ITypedTransition> getOutboundTransitions();
}
