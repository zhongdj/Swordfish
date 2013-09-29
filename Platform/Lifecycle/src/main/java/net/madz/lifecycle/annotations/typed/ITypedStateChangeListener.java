package net.madz.lifecycle.annotations.typed;


/**
 * IStateChangeListener interface is an observer interface, which is used to
 * receive state change events.
 * 
 * @author barry
 * 
 */
public interface ITypedStateChangeListener {

    /**
     * After state successfully changed, the corresponding state change event
     * will be fired to trigger listeners.
     * 
     * This method should be executed within a different thread other than the
     * thread performs state changes.
     * 
     * Exception should not be thrown from this method, or will be ignored.
     * 
     * @param context
     */
    void onStateChanged(TypedStateContext<?, ?> context);
}
