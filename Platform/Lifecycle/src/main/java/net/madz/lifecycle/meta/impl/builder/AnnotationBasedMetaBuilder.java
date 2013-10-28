package net.madz.lifecycle.meta.impl.builder;

import net.madz.meta.MetaData;
import net.madz.meta.MetaDataBuilder;
import net.madz.meta.impl.MetaDataBuilderBase;

public abstract class AnnotationBasedMetaBuilder<SELF extends MetaData, PARENT extends MetaData> extends
        MetaDataBuilderBase<SELF, PARENT> implements MetaDataBuilder<SELF, PARENT> {

    protected AnnotationBasedMetaBuilder(PARENT parent, String name) {
        super(parent, name);
    }
}