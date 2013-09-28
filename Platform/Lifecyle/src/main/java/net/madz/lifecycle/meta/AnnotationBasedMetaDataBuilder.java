package net.madz.lifecycle.meta;

import java.lang.reflect.AnnotatedElement;

import net.madz.meta.MetaData;
import net.madz.meta.MetaDataBuilder;

public interface AnnotationBasedMetaDataBuilder<SELF extends MetaData, PARENT extends MetaData> extends MetaDataBuilder<SELF, PARENT> {

    SELF build(PARENT parent, AnnotatedElement element);

}
