package net.madz.lifecycle.meta.instance;

import java.lang.reflect.Method;

import net.madz.lifecycle.meta.MetaObject;
import net.madz.lifecycle.meta.MultiKeyed;
import net.madz.lifecycle.meta.template.ConditionMetadata;

public interface ConditionObject extends MetaObject<ConditionObject, ConditionMetadata>, MultiKeyed {

    Method conditionGetter();
}
