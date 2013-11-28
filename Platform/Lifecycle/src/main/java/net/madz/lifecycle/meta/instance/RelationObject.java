package net.madz.lifecycle.meta.instance;

import net.madz.lifecycle.meta.MetaObject;
import net.madz.lifecycle.meta.MultiKeyed;
import net.madz.lifecycle.meta.template.RelationMetadata;

public interface RelationObject extends MetaObject<RelationObject, RelationMetadata>, MultiKeyed {

    ReadAccessor<?> getEvaluator();

}
