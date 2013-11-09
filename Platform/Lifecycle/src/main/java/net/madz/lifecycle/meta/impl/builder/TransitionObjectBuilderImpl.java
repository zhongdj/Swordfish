package net.madz.lifecycle.meta.impl.builder;

import java.lang.reflect.Method;

import net.madz.lifecycle.meta.builder.StateMachineObjectBuilder;
import net.madz.lifecycle.meta.builder.TransitionObjectBuilder;
import net.madz.lifecycle.meta.template.TransitionMetadata;
import net.madz.meta.MetaData;
import net.madz.meta.MetaDataFilter;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailureSet;

public class TransitionObjectBuilderImpl extends
        AnnotationMetaBuilderBase<TransitionObjectBuilder, StateMachineObjectBuilder> implements
        TransitionObjectBuilder {

    private Method transitionMethod;
    private TransitionMetadata template;

    public TransitionObjectBuilderImpl(StateMachineObjectBuilder parent, Method transitionMethod,
            TransitionMetadata template) {
        super(parent, "TransitionSet." + template.getDottedPath().getName() + "." + transitionMethod.getName());
        this.transitionMethod = transitionMethod;
        this.template = template;
    }

    @Override
    public TransitionObjectBuilder build(Class<?> klass, StateMachineObjectBuilder parent) throws VerificationException {
        addKeys(template.getKeySet());
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
    public TransitionObjectBuilder filter(MetaData parent, MetaDataFilter filter, boolean lazyFilter) {
        return this;
    }
}
