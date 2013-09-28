package net.madz.lifecycle.meta.impl;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import net.madz.lifecycle.IState;
import net.madz.lifecycle.annotations.StateMachine;
import net.madz.lifecycle.annotations.StateSet;
import net.madz.lifecycle.annotations.Transition;
import net.madz.lifecycle.annotations.TransitionSet;
import net.madz.lifecycle.meta.StateMachineMetaData;
import net.madz.lifecycle.meta.StateMachineMetaDataBuilder;
import net.madz.lifecycle.meta.StateMetaData;
import net.madz.lifecycle.meta.StateMetaDataBuilder;
import net.madz.lifecycle.meta.TransitionMetaData;
import net.madz.meta.MetaData;
import net.madz.meta.impl.MetaDataBuilderBase;
import net.madz.verification.VerificationFailureSet;

public class StateMachineMetaDataBuilderImpl extends MetaDataBuilderBase<StateMachineMetaData<?, ?, ?>, MetaData> implements StateMachineMetaDataBuilder {

    private StateMachineMetaDataImpl<?, ?, ?> stateMachineMetaData;

    public StateMachineMetaDataBuilderImpl(MetaData parent, String name) {
        super(parent, name);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public synchronized StateMachineMetaData<?, ?, ?> build(MetaData parent, AnnotatedElement element) {
        if (!(element instanceof Class)) {
            throw new IllegalArgumentException("Should be interface class.");
        }
        final StateMachine stateMachine = element.getAnnotation(StateMachine.class);
        if (null == stateMachine) {
            throw new IllegalArgumentException("No StateMachine Annotation found.");
        }

        Class reactiveObjectClass = (Class) element;

        final StateSet states = stateMachine.states();
        Class<? extends IState> stateEnumClass = states.value();

        stateMachineMetaData = new StateMachineMetaDataImpl(parent, reactiveObjectClass, stateEnumClass);

        StateMetaData initialState = null;
        final Field[] stateFields = stateEnumClass.getFields();
        final ArrayList<StateMetaData<?, ?>> finalStateList = new ArrayList<StateMetaData<?, ?>>();
        final ArrayList<StateMetaData<?, ?>> transientStateList = new ArrayList<StateMetaData<?, ?>>();
        for (Field stateField : stateFields) {
            if (!stateEnumClass.isAssignableFrom(stateField.getType()) || !Modifier.isStatic(stateField.getModifiers())) {
                continue;
            }

            final StateMetaDataBuilder stateMetaDataBuilder = new StateMetaDataBuilderImpl(stateMachineMetaData, stateField.getName());
            final StateMetaData stateMetaData = stateMetaDataBuilder.build(stateMachineMetaData, stateField);
            switch (stateMetaData.getType()) {
            case Initial:
                initialState = stateMetaData;
                break;
            case End:
                finalStateList.add(stateMetaData);
                break;
            default:
                transientStateList.add(stateMetaData);
                break;
            }

        }

        stateMachineMetaData.setInitialState(initialState);

        TransitionMetaData corruptTransition = null;
        TransitionMetaData recoverTransition = null;
        TransitionMetaData redoTransition = null;

        final TransitionSet transitions = stateMachine.transitions();
        final Class transitionEnumClass = transitions.value();
        final Field[] transitionFields = transitionEnumClass.getFields();
        final ArrayList<TransitionMetaData> transitionList = new ArrayList<TransitionMetaData>();
        for (Field transitionField : transitionFields) {
            if (!transitionEnumClass.isAssignableFrom(transitionField.getType()) || !Modifier.isStatic(transitionField.getModifiers())) {
                continue;
            }

            final TransitionMetaDataBuilderImpl transitionMetaDataBuilder = new TransitionMetaDataBuilderImpl(stateMachineMetaData, transitionField.getName());
            final TransitionMetaDataImpl transitionMetaData = (TransitionMetaDataImpl) transitionMetaDataBuilder.build(stateMachineMetaData, transitionField);

            final String transitionName = transitionField.getName();
            Method[] methods = reactiveObjectClass.getMethods();
            for (Method method : methods) {
                if (method.getName().equalsIgnoreCase(transitionName)) {
                    transitionMetaData.setTransitionMethod(method);
                    break;
                } else {
                    final Transition transitionAnnotation = method.getAnnotation(Transition.class);
                    if (null != transitionAnnotation) {
                        if (!Transition.NULL.equals(transitionAnnotation.value())) {
                            if (method.getName().equalsIgnoreCase(transitionAnnotation.value())) {
                                transitionMetaData.setTransitionMethod(method);
                                break;
                            }
                        }
                    }
                }
            }

            switch (transitionMetaData.getType()) {
            case Corrupt:
                corruptTransition = transitionMetaData;
                break;
            case Recover:
                recoverTransition = transitionMetaData;
                break;
            case Redo:
                redoTransition = transitionMetaData;
                break;
            default:
                break;
            }
            transitionList.add(transitionMetaData);
        }

        for (TransitionMetaData transition : transitionList) {
            stateMachineMetaData.addTransition(transition);
        }

        for (StateMetaData state : finalStateList) {
            stateMachineMetaData.addFinalState(state);
        }

        for (StateMetaData state : transientStateList) {
            stateMachineMetaData.addTransientState(state);
        }

        stateMachineMetaData.setCorruptTransition(corruptTransition);
        stateMachineMetaData.setRecoverTransition(recoverTransition);
        stateMachineMetaData.setRedoTransition(redoTransition);

        return stateMachineMetaData;
    }

    @Override
    public synchronized void verifyMetaData(VerificationFailureSet verificationSet) {
        this.stateMachineMetaData.verifyMetaData(verificationSet);
    }
}
