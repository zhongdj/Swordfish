/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.madz.core.entities;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import net.madz.core.annotations.EntityAnnotationProcessor;
import net.madz.core.annotations.ExtendEntityAnnotationProcessor;

/**
 * 
 * @author Barry
 */
@MappedSuperclass
@Access(AccessType.FIELD)
public abstract class AbstractBaseEntity implements Serializable {

    private static final long serialVersionUID = -6885878862729201814L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    protected long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @PrePersist
    private void prePersistCallback() {
        processEntityAnnotationsAt(PrePersist.class);
    }

    @PreUpdate
    private void preUpdateCallback() {
        processEntityAnnotationsAt(PreUpdate.class);
    }

    @SuppressWarnings({ "unchecked" })
    private void processEntityAnnotationsAt(Class<? extends Annotation> timing) {
        final ArrayList<Pair> processors = findProcessorsAt(timing);
        for ( Pair pair : processors ) {
            pair.p.processAnnotation(this, pair.a);
        }
    }

    @SuppressWarnings({ "rawtypes" })
    private ArrayList<Pair> findProcessorsAt(Class<? extends Annotation> timing) {
        final ArrayList<Pair> processors = new ArrayList<>();
        for ( Class<?> c = getClass(); !c.equals(Object.class); c = c.getSuperclass() ) {
            for ( final Annotation annotationOnEntity : c.getAnnotations() ) {
                final ExtendEntityAnnotationProcessor extEntityProcessorAnnotation = annotationOnEntity
                        .annotationType().getAnnotation(ExtendEntityAnnotationProcessor.class);
                if ( null == extEntityProcessorAnnotation ) {
                    continue;
                }
                if ( contains(timing, extEntityProcessorAnnotation) ) {
                    try {
                        final EntityAnnotationProcessor processor = extEntityProcessorAnnotation.value().newInstance();
                        processors.add(new Pair(annotationOnEntity, processor));
                    } catch (Exception e) {
                        Logger.getLogger(getClass().getName()).log(
                                Level.SEVERE,
                                "Failed to initialize processor instances of class : "
                                        + extEntityProcessorAnnotation.value().getName(), e);
                    }
                }
            }
        }
        Collections.reverse(processors);
        return processors;
    }

    private boolean contains(Class<?> timing, ExtendEntityAnnotationProcessor processorAnnotation) {
        for ( Class<? extends Annotation> callback : processorAnnotation.callbackAt() ) {
            if ( callback == timing ) {
                return true;
            }
        }
        return false;
    }

    private class Pair {

        public final Annotation a;

        @SuppressWarnings("rawtypes")
        public final EntityAnnotationProcessor p;

        @SuppressWarnings("rawtypes")
        public Pair(Annotation a, EntityAnnotationProcessor p) {
            this.a = a;
            this.p = p;
        }
    }
}
