package net.madz.lifecycle.meta.impl.builder;

import java.lang.reflect.Method;

import net.madz.lifecycle.meta.builder.ConditionObjectBuilder;
import net.madz.lifecycle.meta.builder.StateMachineObjectBuilder;
import net.madz.lifecycle.meta.template.ConditionMetadata;
import net.madz.meta.MetaData;
import net.madz.meta.MetaDataFilter;
import net.madz.meta.MetaDataFilterable;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailureSet;

public class ConditionObjectBuilderImpl extends ObjectBuilderBase<ConditionObjectBuilder, StateMachineObjectBuilder>
        implements ConditionObjectBuilder {

    private ConditionMetadata template;
    private Method conditionGetter;

    protected ConditionObjectBuilderImpl(StateMachineObjectBuilder parent, Method method, ConditionMetadata template) {
        super(parent, "ConditionSet." + template.getDottedPath().getName());
        this.template = template;
        this.conditionGetter = method;
    }

    @Override
    public Method conditionGetter() {
        return conditionGetter;
    }

    @Override
    public ConditionMetadata getTemplate() {
        return template;
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {}

    @Override
    public ConditionObjectBuilder build(Class<?> klass, StateMachineObjectBuilder parent) throws VerificationException {
        addKeys(template.getKeySet());
        return this;
    }

    @Override
    public MetaDataFilterable filter(MetaData parent, MetaDataFilter filter, boolean lazyFilter) {
        return this;
    }
}
