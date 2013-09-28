package net.madz.lifecycle;

/**
 * IStateChangeInterceptor provides callback intercept mechanism before a state
 * change event happens.
 * 
 * @author barry
 * 
 */
public interface IStateChangeInterceptor {

    /**
     * This method will be invoked within the same thread of which will also
     * performs state changes and before state changes.
     * 
     * Exceptions can be thrown from this method to prevent the target state
     * change.
     * 
     * @param context
     */
    void interceptStateChange(StateContext<?, ?> context);
}
