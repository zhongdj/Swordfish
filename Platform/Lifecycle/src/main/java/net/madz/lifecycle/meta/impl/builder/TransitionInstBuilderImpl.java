package net.madz.lifecycle.meta.impl.builder;

import java.lang.reflect.Method;

import net.madz.lifecycle.meta.builder.StateMachineInstBuilder;
import net.madz.lifecycle.meta.builder.TransitionInstBuilder;
import net.madz.lifecycle.meta.template.TransitionMetadata;
import net.madz.meta.MetaData;
import net.madz.meta.MetaDataFilter;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailureSet;

public class TransitionInstBuilderImpl extends
        AnnotationMetaBuilderBase<TransitionInstBuilder, StateMachineInstBuilder> implements TransitionInstBuilder {

    private Method transitionMethod;
    private TransitionMetadata template;

    public TransitionInstBuilderImpl(StateMachineInstBuilder parent, Method transitionMethod,
            TransitionMetadata template) {
        super(parent, "TransitionSet" + template.getDottedPath().getName() + transitionMethod.getName());
        this.transitionMethod = transitionMethod;
        this.template = template;
    }

    @Override
    public TransitionInstBuilder build(Class<?> klass, StateMachineInstBuilder parent) throws VerificationException {
        return this;
    }

    @Override
    public Method getTransitionMethod() {
        return transitionMethod;
    }

    @Override
    public TransitionMetadata getTemplate() {
        return template;
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {
        // TODO Auto-generated method stub
    }

    @Override
    public TransitionInstBuilder filter(MetaData parent, MetaDataFilter filter, boolean lazyFilter) {
        return this;
    }
}
