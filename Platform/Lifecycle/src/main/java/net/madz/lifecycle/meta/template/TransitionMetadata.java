package net.madz.lifecycle.meta.template;

import net.madz.common.Dumpable;
import net.madz.lifecycle.annotations.action.ConditionalTransition;
import net.madz.lifecycle.meta.Template;
import net.madz.lifecycle.meta.instance.TransitionObject;
import net.madz.meta.MetaData;
import net.madz.meta.MetaDataFilterable;

public interface TransitionMetadata extends MetaData, Dumpable, Template<TransitionObject>, MetaDataFilterable {

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
}
