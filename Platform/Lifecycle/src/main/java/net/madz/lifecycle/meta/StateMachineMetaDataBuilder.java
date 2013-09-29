package net.madz.lifecycle.meta;

import java.lang.reflect.AnnotatedElement;

import net.madz.meta.MetaData;
import net.madz.meta.MetaDataBuilder;

public interface StateMachineMetaDataBuilder extends MetaDataBuilder<StateMachineMetaData<?, ?, ?>, MetaData> {

    StateMachineMetaData<?, ?, ?> build(MetaData parent, AnnotatedElement element);

}