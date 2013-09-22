package net.madz.core.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ExtendEntityAnnotationProcessor {

	Class<? extends EntityAnnotationProcessor> value();

	Class<? extends Annotation>[] callbackAt();
}
