package net.madz.lifecycle.meta.impl;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

import net.madz.lifecycle.IReactiveObject;
import net.madz.lifecycle.IState;
import net.madz.lifecycle.annotations.action.End;
import net.madz.lifecycle.annotations.state.Corrupted;
import net.madz.lifecycle.annotations.state.Initial;
import net.madz.lifecycle.annotations.state.Running;
import net.madz.lifecycle.annotations.state.Stopped;
import net.madz.lifecycle.annotations.state.Waiting;
import net.madz.lifecycle.meta.StateMachineMetaData;
import net.madz.lifecycle.meta.StateMetaData;
import net.madz.lifecycle.meta.StateMetaDataBuilder;
import net.madz.lifecycle.meta.StateMetaData.StateTypeEnum;
import net.madz.meta.impl.MetaDataBuilderBase;
import net.madz.verification.VerificationFailureSet;

public class StateMetaDataBuilderImpl<R extends IReactiveObject, S extends IState<R, S>> extends
        MetaDataBuilderBase<StateMetaData<R, S>, StateMachineMetaData<R, S, ?>> implements StateMetaDataBuilder<R, S> {

    private StateMetaDataImpl<R, S> stateMetaDataImpl;

    protected StateMetaDataBuilderImpl(StateMachineMetaData<R, S, ?> parent, String name) {
        super(parent, name);
    }

    @Override
    public StateMetaData<R, S> build(StateMachineMetaData<R, S, ?> parent, AnnotatedElement element) {

        if (!(element instanceof Field)) {
            throw new IllegalArgumentException("ONLY accept Field type element.");
        }

        Field stateField = (Field) element;
        final StateTypeEnum typeEnum;
        if (null != stateField.getAnnotation(Initial.class)) {
            typeEnum = StateTypeEnum.Initial;
        } else if (null != stateField.getAnnotation(End.class)) {
            typeEnum = StateTypeEnum.End;
        } else if (null != stateField.getAnnotation(Running.class)) {
            typeEnum = StateTypeEnum.Running;
        } else if (null != stateField.getAnnotation(Stopped.class)) {
            typeEnum = StateTypeEnum.Stopped;
        } else if (null != stateField.getAnnotation(Corrupted.class)) {
            typeEnum = StateTypeEnum.Corrupted;
        } else if (null != stateField.getAnnotation(Waiting.class)) {
            typeEnum = StateTypeEnum.Waiting;
        } else {
            typeEnum = StateTypeEnum.Unknown;
        }

        final Class<?> stateEnumClass = stateField.getDeclaringClass();

        try {
            @SuppressWarnings("unchecked")
            final S state = (S) stateField.get(stateEnumClass);
            stateMetaDataImpl = new StateMetaDataImpl<R, S>(parent, state, typeEnum, this.path.getName());
            return stateMetaDataImpl;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Cannot get value from Enum Class:" + stateEnumClass.getName() + " Field: " + stateField.getName(), ex);
        }
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {
        stateMetaDataImpl.verifyMetaData(verificationSet);
    }

}
