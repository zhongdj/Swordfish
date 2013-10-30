package net.madz.lifecycle.meta.template;

import net.madz.common.Dumpable;
import net.madz.lifecycle.meta.Template;
import net.madz.lifecycle.meta.instance.TransitionInst;
import net.madz.meta.MetaData;
import net.madz.meta.MetaDataFilterable;

public interface TransitionMetadata extends MetaData, Dumpable, Template<TransitionInst>, MetaDataFilterable {

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
}
