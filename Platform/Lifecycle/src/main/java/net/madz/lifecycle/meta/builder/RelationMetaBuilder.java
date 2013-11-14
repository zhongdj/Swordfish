package net.madz.lifecycle.meta.builder;

import net.madz.lifecycle.meta.template.RelationMetadata;
import net.madz.lifecycle.meta.template.StateMetadata;

public interface RelationMetaBuilder extends RelationMetadata, AnnotationMetaBuilder<RelationMetadata, StateMetadata> {}
