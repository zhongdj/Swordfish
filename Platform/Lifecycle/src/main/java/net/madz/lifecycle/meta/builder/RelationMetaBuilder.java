package net.madz.lifecycle.meta.builder;

import net.madz.lifecycle.meta.template.RelationMetadata;
import net.madz.meta.MetaDataBuilder;

public interface RelationMetaBuilder extends MetaDataBuilder<RelationMetaBuilder, StateMetaBuilder>,
        RelationMetadata, AnnotationMetaBuilder<RelationMetaBuilder, StateMetaBuilder> {
}
