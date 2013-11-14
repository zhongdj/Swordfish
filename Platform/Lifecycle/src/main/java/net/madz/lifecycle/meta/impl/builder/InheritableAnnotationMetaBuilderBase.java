package net.madz.lifecycle.meta.impl.builder;

import net.madz.lifecycle.meta.Inheritable;
import net.madz.lifecycle.meta.builder.AnnotationMetaBuilder;
import net.madz.meta.MetaData;

public abstract class InheritableAnnotationMetaBuilderBase<SELF extends MetaData, PARENT extends MetaData> extends
        AnnotationMetaBuilderBase<SELF, PARENT> implements AnnotationMetaBuilder<SELF, PARENT>, Inheritable<SELF> {

    private SELF superMeta;
    private boolean overriding;

    protected InheritableAnnotationMetaBuilderBase(PARENT parent, String name) {
        super(parent, name);
        System.out.println(getDottedPath().getAbsoluteName());
    }

    @Override
    public SELF getSuper() {
        return superMeta;
    }

    @Override
    public boolean isOverriding() {
        return overriding;
    }
}