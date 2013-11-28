package net.madz.lifecycle.meta.impl.builder;

import java.lang.reflect.Method;

import net.madz.lifecycle.meta.builder.ConditionObjectBuilder;
import net.madz.lifecycle.meta.builder.StateMachineObjectBuilder;
import net.madz.lifecycle.meta.instance.ConditionObject;
import net.madz.lifecycle.meta.instance.StateMachineObject;
import net.madz.lifecycle.meta.template.ConditionMetadata;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailureSet;

public class ConditionObjectBuilderImpl extends ObjectBuilderBase<ConditionObject, StateMachineObject<?>, ConditionMetadata> implements ConditionObjectBuilder {

    private Method conditionGetter;

    protected ConditionObjectBuilderImpl(StateMachineObjectBuilder<?> parent, Method method, ConditionMetadata template) {
        super(parent, "ConditionSet." + template.getDottedPath().getName());
        this.setMetaType(template);
        this.conditionGetter = method;
    }

    @Override
    public Method conditionGetter() {
        return conditionGetter;
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {}

    @Override
    public ConditionObjectBuilder build(Class<?> klass, StateMachineObject<?> parent) throws VerificationException {
        super.build(klass, parent);
        return this;
    }
}
