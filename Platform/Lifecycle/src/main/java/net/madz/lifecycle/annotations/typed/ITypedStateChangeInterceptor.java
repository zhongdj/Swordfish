package net.madz.lifecycle.annotations.typed;


/**
 * IStateChangeInterceptor provides callback intercept mechanism before a state
 * change event happens.
 * 
 * @author barry
 * 
 */
public interface ITypedStateChangeInterceptor {

    /**
     * This method will be invoked within the same thread of which will also
     * performs state changes and before state changes.
     * 
     * Exceptions can be thrown from this method to prevent the target state
     * change.
     * 
     * @param context
     */
    void interceptStateChange(TypedStateContext<?, ?> context);
}
