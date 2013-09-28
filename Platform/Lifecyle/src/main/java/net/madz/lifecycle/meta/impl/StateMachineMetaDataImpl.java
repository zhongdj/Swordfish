package net.madz.lifecycle.meta.impl;

import java.lang.reflect.Field;
import java.util.*;

import net.madz.common.DottedPath;
import net.madz.common.Dumper;
import net.madz.common.ParameterString;
import net.madz.lifecycle.IReactiveObject;
import net.madz.lifecycle.IState;
import net.madz.lifecycle.ITransition;
import net.madz.lifecycle.meta.StateMachineMetaData;
import net.madz.lifecycle.meta.StateMetaData;
import net.madz.lifecycle.meta.TransitionMetaData;
import net.madz.lifecycle.meta.StateMetaData.StateTypeEnum;
import net.madz.meta.KeySet;
import net.madz.meta.MetaData;
import net.madz.meta.MetaDataFilter;
import net.madz.meta.MetaDataFilterable;
import net.madz.verification.VerificationFailureSet;

public class StateMachineMetaDataImpl<R extends IReactiveObject, S extends IState<R, S>, T extends ITransition> implements MetaData,
        StateMachineMetaData<R, S, T> {

    protected Class<R> reactiveObjectClass;
    protected Class<S> stateEnumClass;
    protected Class<T> transitionEnumClass;

    protected StateMetaData<R, S> initialState;
    protected final List<StateMetaData<R, S>> allStates = new ArrayList<StateMetaData<R, S>>();
    protected final List<StateMetaData<R, S>> finalStates = new ArrayList<StateMetaData<R, S>>();;
    protected final List<StateMetaData<R, S>> transientStates = new ArrayList<StateMetaData<R, S>>();
    protected final HashMap<S, StateMetaData<R, S>> stateIndexMap = new HashMap<S, StateMetaData<R, S>>();

    protected final List<TransitionMetaData> allTransitions = new ArrayList<TransitionMetaData>();;
    protected TransitionMetaData corruptTransition;
    protected TransitionMetaData recoverTransition;
    protected TransitionMetaData redoTransition;
    protected final HashMap<ITransition, TransitionMetaData> transitionIndexMap = new HashMap<ITransition, TransitionMetaData>();
    protected final MetaData parent;
    protected final DottedPath dottedPath;
    protected final KeySet keySet;

    public StateMachineMetaDataImpl(MetaData parent, Class<R> reactiveObjectClass, Class<S> stateEnumClass) {

        this.parent = parent;

        if (null == parent) {
            this.dottedPath = DottedPath.parse(reactiveObjectClass.getName() + ".StateMachine");
        } else {
            this.dottedPath = parent.getDottedPath().append(reactiveObjectClass.getName() + ".StateMachine");
        }
        this.reactiveObjectClass = reactiveObjectClass;
        this.stateEnumClass = stateEnumClass;
        this.keySet = new KeySet(this.dottedPath.getAbsoluteName());
    }

    public Class<T> getTransitionEnumClass() {
        return transitionEnumClass;
    }

    public void setTransitionEnumClass(Class<T> transitionEnumClass) {
        this.transitionEnumClass = transitionEnumClass;
    }

    @SuppressWarnings("unchecked")
    public T getTranstion(String name) throws Exception {
        Field enumField = transitionEnumClass.getField(name);
        return (T) enumField.get(transitionEnumClass);
    }

    @Override
    public StateMetaData<R, S> getInitialState() {
        return initialState;
    }

    public void setInitialState(StateMetaData<R, S> initialState) {
        this.initialState = initialState;
        addState(initialState);
    }

    @Override
    public void addFinalState(StateMetaData<R, S> finalState) {
        this.finalStates.add(finalState);
        addState(finalState);
    }

    private void addState(StateMetaData<R, S> state) {
        this.allStates.add(state);
        this.stateIndexMap.put(state.getState(), state);
    }

    @Override
    public void addTransientState(StateMetaData<R, S> state) {
        this.transientStates.add(state);
        addState(state);
    }

    @Override
    public void addTransition(TransitionMetaData transition) {
        this.allTransitions.add(transition);
        this.transitionIndexMap.put(transition.getTransition(), transition);
    }

    @Override
    public TransitionMetaData getCorruptTransition() {
        return corruptTransition;
    }

    public void setCorruptTransition(TransitionMetaData corruptTransition) {
        this.corruptTransition = corruptTransition;
        this.addTransition(corruptTransition);
    }

    @Override
    public TransitionMetaData getRecoverTransition() {
        return recoverTransition;
    }

    public void setRecoverTransition(TransitionMetaData recoverTransition) {
        this.recoverTransition = recoverTransition;
        this.addTransition(recoverTransition);
    }

    @Override
    public TransitionMetaData getRedoTransition() {
        return redoTransition;
    }

    public void setRedoTransition(TransitionMetaData redoTransition) {
        this.redoTransition = redoTransition;
        this.addTransition(redoTransition);
    }

    @Override
    public StateMetaData<R, S> getStateMetaData(S state) {
        return this.stateIndexMap.get(state);
    }

    @Override
    public TransitionMetaData getTransitionMetaData(ITransition transition) {
        return this.transitionIndexMap.get(transition);
    }

    @Override
    public MetaData getParent() {
        return parent;
    }

    @Override
    public DottedPath getDottedPath() {
        return dottedPath;
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {
        for (StateMetaData<R, S> m : allStates) {
            m.verifyMetaData(verificationSet);
        }

        for (TransitionMetaData m : allTransitions) {
            m.verifyMetaData(verificationSet);
        }

    }

    @Override
    public void dump(Dumper dumper) {
        dumper.println(toString()).indent().print("allStates=").dump(Collections.unmodifiableList(allStates)).print("allTransitions=")
                .dump(Collections.unmodifiableList(allTransitions)).print("initialState=").dump(initialState).print("allFinalStates=")
                .dump(Collections.unmodifiableList(finalStates)).print("allTransientStates=").dump(Collections.unmodifiableList(transientStates))
                .print("allRunningStates=").dump(getRunningStateMetaData()).print("allStoppedStates=").dump(getStoppedStateMetaData())
                .print("allWaitingStates=").dump(getWaitingStateMetaData()).print("corruptTransition=").dump(corruptTransition).print("recoverTransition=")
                .dump(recoverTransition).print("redoTransition=").dump(recoverTransition);

    }

    @Override
    public final String toString() {
        return toString(new ParameterString(getClass().getSimpleName())).toString();
    }

    public ParameterString toString(ParameterString sb) {
        sb.append("name", this.dottedPath.getAbsoluteName());
        sb.append("states", new TreeMap<S, StateMetaData<R, S>>(this.stateIndexMap));
        sb.append("transition", new TreeMap<Object, TransitionMetaData>(this.transitionIndexMap));
        return sb;
    }

    @Override
    public T getTransition(String name) {
        for (TransitionMetaData t : allTransitions) {
            if (t.getDottedPath().getName().equals(name)) {
                return t.getTransition();
            }
        }
        throw new IllegalStateException("Cannot find transition: " + name + " within stateMachine: " + getDottedPath().getAbsoluteName());
    }

    @SuppressWarnings("unchecked")
    public StateMetaData<R, S>[] getRunningStateMetaData() {
        final ArrayList<StateMetaData<R, S>> result = new ArrayList<StateMetaData<R, S>>();
        for (StateMetaData<R, S> m : allStates) {
            if (StateTypeEnum.Running == m.getType()) {
                result.add(m);
            }
        }
        return result.toArray(new StateMetaData[result.size()]);
    }

    @SuppressWarnings("unchecked")
    public StateMetaData<R, S>[] getCorruptedStateMetaData() {
        final ArrayList<StateMetaData<R, S>> result = new ArrayList<StateMetaData<R, S>>();
        for (StateMetaData<R, S> m : allStates) {
            if (StateTypeEnum.Corrupted == m.getType()) {
                result.add(m);
            }
        }
        return result.toArray(new StateMetaData[result.size()]);
    }

    @SuppressWarnings("unchecked")
    public StateMetaData<R, S>[] getStoppedStateMetaData() {
        final ArrayList<StateMetaData<R, S>> result = new ArrayList<StateMetaData<R, S>>();
        for (StateMetaData<R, S> m : allStates) {
            if (StateTypeEnum.Stopped == m.getType()) {
                result.add(m);
            }
        }
        return result.toArray(new StateMetaData[result.size()]);
    }

    @SuppressWarnings("unchecked")
    public StateMetaData<R, S>[] getWaitingStateMetaData() {
        final ArrayList<StateMetaData<R, S>> result = new ArrayList<StateMetaData<R, S>>();
        for (StateMetaData<R, S> m : allStates) {
            if (StateTypeEnum.Waiting == m.getType()) {
                result.add(m);
            }
        }
        return result.toArray(new StateMetaData[result.size()]);
    }

    @Override
    public MetaDataFilterable filter(MetaData parent, MetaDataFilter filter, boolean lazyFilter) {
        return this;
    }

    @Override
    public KeySet getKeySet() {
        return this.keySet;
    }

}
