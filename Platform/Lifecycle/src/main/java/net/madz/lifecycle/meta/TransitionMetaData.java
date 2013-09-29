package net.madz.lifecycle.meta;

import java.lang.reflect.Method;

import net.madz.common.Dumpable;
import net.madz.lifecycle.annotations.typed.ITypedTransition;
import net.madz.meta.MetaData;

public interface TransitionMetaData extends MetaData, Dumpable {

    public static enum TransitionTypeEnum {
        Corrupt, Recover, Redo, Other
    }

    TransitionTypeEnum getType();

    <T extends ITypedTransition> T getTransition();

    long getTimeout();

    Method getTransitionMethod();
}