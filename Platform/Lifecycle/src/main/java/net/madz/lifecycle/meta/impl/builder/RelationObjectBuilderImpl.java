package net.madz.lifecycle.meta.impl.builder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.madz.lifecycle.meta.FieldEvaluator;
import net.madz.lifecycle.meta.PropertyEvaluator;
import net.madz.lifecycle.meta.builder.RelationObjectBuilder;
import net.madz.lifecycle.meta.instance.RelationObject;
import net.madz.lifecycle.meta.instance.StateMachineObject;
import net.madz.lifecycle.meta.template.RelationMetadata;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailureSet;

public class RelationObjectBuilderImpl extends ObjectBuilderBase<RelationObject, StateMachineObject<?>, RelationMetadata> implements RelationObjectBuilder {

    private ReadAccessor<Object> evaluator = null;

    private RelationObjectBuilderImpl(final StateMachineObject<?> parent, final String name) {
        super(parent, name);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public RelationObjectBuilderImpl(final StateMachineObject parent, final Field field, RelationMetadata template) {
        this(parent, "RelationSet." + template.getDottedPath().getName() + "." + field.getName());
        evaluator = new FieldEvaluator(field);
        setMetaType(template);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public RelationObjectBuilderImpl(final StateMachineObject parent, Method method, RelationMetadata template) {
        this(parent, "RelationSet." + template.getDottedPath().getName() + "." + method.getName());
        evaluator = new PropertyEvaluator(method);
        setMetaType(template);
    }

    @Override
    public RelationObjectBuilder build(Class<?> klass, StateMachineObject<?> parent) throws VerificationException {
        super.build(klass, parent);
        return this;
    }

    @Override
    public ReadAccessor<Object> getEvaluator() {
        return this.evaluator;
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {
        // TODO Auto-generated method stub
    }
}
