package net.madz.lifecycle.annotations.typed;

import java.io.Serializable;

@SuppressWarnings("rawtypes")
public final class TypedStateContext<R extends ITypedReactiveObject, S extends ITypedState> implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    private final R reactiveObject;
    private final S currentState;
    private final S nextState;
    private final ITypedTransition transition;
    private final Object[] transitionArgs;

    public TypedStateContext(R reactiveObject, S nextState, ITypedTransition ongoingTransition, Object[] transitionArgs) {
        this.reactiveObject = reactiveObject;
        this.currentState = this.reactiveObject.getState();
        this.nextState = nextState;
        this.transition = ongoingTransition;
        this.transitionArgs = transitionArgs;
    }

    public S getCurrentState() {
        return this.currentState;
    }

    public R getReactiveObject() {
        return reactiveObject;
    }

    public S getNextState() {
        return nextState;
    }

    public ITypedTransition getTransition() {
        return transition;
    }

    public Object[] getTransitionArgs() {
        return transitionArgs;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
