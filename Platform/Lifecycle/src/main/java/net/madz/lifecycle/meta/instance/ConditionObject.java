package net.madz.lifecycle.meta.instance;

import java.lang.reflect.Method;

import net.madz.lifecycle.meta.MetaObject;
import net.madz.lifecycle.meta.template.ConditionMetadata;
import net.madz.meta.FlavorMetaData;

public interface ConditionObject extends MetaObject<ConditionObject, ConditionMetadata>, FlavorMetaData<StateMachineObject<?>> {

    Method conditionGetter();
}
