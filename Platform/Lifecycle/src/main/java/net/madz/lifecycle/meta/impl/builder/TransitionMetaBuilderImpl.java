package net.madz.lifecycle.meta.impl.builder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import net.madz.common.Dumper;
import net.madz.lifecycle.SyntaxErrors;
import net.madz.lifecycle.annotations.action.Conditional;
import net.madz.lifecycle.annotations.action.ConditionalTransition;
import net.madz.lifecycle.annotations.action.Corrupt;
import net.madz.lifecycle.annotations.action.Fail;
import net.madz.lifecycle.annotations.action.Recover;
import net.madz.lifecycle.annotations.action.Redo;
import net.madz.lifecycle.meta.builder.TransitionMetaBuilder;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.lifecycle.meta.template.TransitionMetadata;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailureSet;

public class TransitionMetaBuilderImpl extends InheritableAnnotationMetaBuilderBase<TransitionMetadata, StateMachineMetadata> implements TransitionMetaBuilder {

    private TransitionTypeEnum type = TransitionTypeEnum.Common;
    private boolean conditional;
    private Class<?> conditionClass;
    private Class<? extends ConditionalTransition<?>> judgerClass;
    private boolean postValidate;

    protected TransitionMetaBuilderImpl(StateMachineMetadata parent, String name) {
        super(parent, "TransitionSet." + name);
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {}

    @Override
    public TransitionMetaBuilder build(Class<?> clazz, StateMachineMetadata parent) throws VerificationException {
        configureSuper(clazz, parent);
        configureCondition(clazz);
        configureType(clazz);
        addKeys(clazz);
        return this;
    }

    private void configureSuper(Class<?> clazz, StateMachineMetadata parent) {}

    private void configureType(Class<?> clazz) {
        if ( null != clazz.getAnnotation(Corrupt.class) ) {
            type = TransitionTypeEnum.Corrupt;
        } else if ( null != clazz.getAnnotation(Redo.class) ) {
            type = TransitionTypeEnum.Redo;
        } else if ( null != clazz.getAnnotation(Recover.class) ) {
            type = TransitionTypeEnum.Recover;
        } else if ( null != clazz.getAnnotation(Fail.class) ) {
            type = TransitionTypeEnum.Fail;
        } else {
            type = TransitionTypeEnum.Common;
        }
    }

    private void configureCondition(Class<?> clazz) throws VerificationException {
        Conditional conditionalAnno = clazz.getAnnotation(Conditional.class);
        if ( null != conditionalAnno ) {
            conditional = true;
            conditionClass = conditionalAnno.condition();
            judgerClass = conditionalAnno.judger();
            postValidate = conditionalAnno.postValidate();
            verifyJudgerClass(clazz, judgerClass, conditionClass);
        } else {
            conditional = false;
        }
    }

    private void verifyJudgerClass(Class<?> clazz, Class<?> judgerClass, Class<?> conditionClass) throws VerificationException {
        for ( Type type : judgerClass.getGenericInterfaces() ) {
            if ( !( type instanceof ParameterizedType ) ) {
                continue;
            }
            final ParameterizedType pType = (ParameterizedType) type;
            if ( isConditionalTransition((Class<?>) pType.getRawType()) && !isConditionClassMatchingJudgerGenericType(conditionClass, pType) ) {
                throw newVerificationException(getDottedPath(), SyntaxErrors.TRANSITION_CONDITIONAL_CONDITION_NOT_MATCH_JUDGER, clazz, conditionClass,
                        judgerClass);
            }
        }
    }

    private boolean isConditionClassMatchingJudgerGenericType(Class<?> conditionClass, final ParameterizedType pType) {
        return conditionClass.isAssignableFrom((Class<?>) pType.getActualTypeArguments()[0]);
    }

    private boolean isConditionalTransition(final Class<?> rawType) {
        return ConditionalTransition.class.isAssignableFrom(rawType);
    }

    @Override
    public StateMachineMetadata getStateMachine() {
        return parent;
    }

    @Override
    public TransitionTypeEnum getType() {
        return type;
    }

    @Override
    public long getTimeout() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void dump(Dumper dumper) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isConditional() {
        return conditional;
    }

    @Override
    public Class<?> getConditionClass() {
        return conditionClass;
    }

    @Override
    public Class<? extends ConditionalTransition<?>> getJudgerClass() {
        return judgerClass;
    }

    @Override
    public boolean postValidate() {
        return postValidate;
    }

    @Override
    protected TransitionMetadata findSuper(Class<?> metaClass) throws VerificationException {
        // TODO Auto-generated method stub
        return null;
    }
}
