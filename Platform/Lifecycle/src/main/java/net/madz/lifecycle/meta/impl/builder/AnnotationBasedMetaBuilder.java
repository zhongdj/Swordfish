package net.madz.lifecycle.meta.impl.builder;

import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.meta.MetaData;
import net.madz.meta.MetaDataBuilder;
import net.madz.meta.impl.MetaDataBuilderBase;

public abstract class AnnotationBasedMetaBuilder<SELF extends MetaData, PARENT extends MetaData> extends
        MetaDataBuilderBase<SELF, PARENT> implements MetaDataBuilder<SELF, PARENT> {


    protected AbsStateMachineRegistry registry;

    public AbsStateMachineRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(AbsStateMachineRegistry registry) {
        this.registry = registry;
    }

    protected AnnotationBasedMetaBuilder(PARENT parent, String name) {
        super(parent, name);
    }
}