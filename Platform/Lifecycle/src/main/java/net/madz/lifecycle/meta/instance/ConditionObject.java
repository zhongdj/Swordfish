package net.madz.lifecycle.meta.instance;

import java.lang.reflect.Method;

import net.madz.lifecycle.meta.Concrete;
import net.madz.lifecycle.meta.template.ConditionMetadata;
import net.madz.meta.FlavorMetaData;

public interface ConditionObject extends Concrete<ConditionMetadata>, FlavorMetaData<StateMachineObject> {

    Method conditionGetter();
}
