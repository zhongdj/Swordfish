package net.madz.lifecycle.meta.instance;

import java.util.LinkedList;

import net.madz.lifecycle.meta.impl.builder.StateMetaBuilderImpl;
import net.madz.lifecycle.meta.template.StateMetadata;
import net.madz.lifecycle.meta.template.TransitionMetadata;

public class FunctionMetadata {

    private final StateMetadata parent;
    private final TransitionMetadata transition;
    private final LinkedList<StateMetadata> nextStates;

    public FunctionMetadata(StateMetaBuilderImpl parent, TransitionMetadata transition,
            LinkedList<StateMetadata> nextStates) {
        this.parent = parent;
        this.transition = transition;
        this.nextStates = nextStates;
    }

    public StateMetadata getParent() {
        return parent;
    }

    public TransitionMetadata getTransition() {
        return transition;
    }

    public LinkedList<StateMetadata> getNextStates() {
        return nextStates;
    }
}
