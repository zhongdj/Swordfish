package net.madz.core.annotations;


public interface EntityAnnotationProcessor<A> {

    void processAnnotation(Object entity, A a);
}
