package net.madz.lifecycle.meta.template;

import net.madz.lifecycle.annotations.action.ConditionalTransition;
import net.madz.lifecycle.meta.MetaType;

public interface TransitionMetadata extends MetaType<TransitionMetadata> {

    StateMachineMetadata getStateMachine();

    public static enum TransitionTypeEnum {
        Corrupt,
        Recover,
        Redo,
        Fail,
        Common,
        Other
    }

    TransitionTypeEnum getType();

    long getTimeout();

    boolean isConditional();

    Class<?> getConditionClass();

    Class<? extends ConditionalTransition<?>> getJudgerClass();

    boolean postValidate();
}
