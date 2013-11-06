package net.madz.lifecycle.meta.impl.builder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import net.madz.common.Dumper;
import net.madz.lifecycle.Errors;
import net.madz.lifecycle.annotations.action.Conditional;
import net.madz.lifecycle.annotations.action.ConditionalTransition;
import net.madz.lifecycle.annotations.action.Corrupt;
import net.madz.lifecycle.annotations.action.Fail;
import net.madz.lifecycle.annotations.action.Recover;
import net.madz.lifecycle.annotations.action.Redo;
import net.madz.lifecycle.meta.builder.StateMachineMetaBuilder;
import net.madz.lifecycle.meta.builder.TransitionMetaBuilder;
import net.madz.lifecycle.meta.instance.TransitionInst;
import net.madz.lifecycle.meta.template.StateMachineMetadata;
import net.madz.meta.MetaData;
import net.madz.meta.MetaDataFilter;
import net.madz.meta.MetaDataFilterable;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailureSet;

public class TransitionMetaBuilderImpl extends
        AnnotationMetaBuilderBase<TransitionMetaBuilder, StateMachineMetaBuilder> implements TransitionMetaBuilder {

    private TransitionTypeEnum type = TransitionTypeEnum.Common;
    private boolean conditional;
    private Class<?> conditionClass;
    private Class<?> judgerClass;

    protected TransitionMetaBuilderImpl(StateMachineMetaBuilder parent, String name) {
        super(parent, "TransitionSet." + name);
    }

    @Override
    public void verifyMetaData(VerificationFailureSet verificationSet) {}

    @Override
    public TransitionMetaBuilder build(Class<?> clazz, StateMachineMetaBuilder parent) throws VerificationException {
        configureCondition(clazz);
        configureType(clazz);
        addKeys(clazz);
        return this;
    }

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
            verifyJudgerClass(clazz, judgerClass, conditionClass);
        } else {
            conditional = false;
        }
    }

    private void verifyJudgerClass(Class<?> clazz, Class<?> judgerClass, Class<?> conditionClass)
            throws VerificationException {
        for ( Type type : judgerClass.getGenericInterfaces() ) {
            if ( type instanceof ParameterizedType ) {
                final ParameterizedType pType = (ParameterizedType) type;
                if ( ConditionalTransition.class.isAssignableFrom((Class<?>) pType.getRawType())) {
                    if (! conditionClass.isAssignableFrom((Class<?>) pType.getActualTypeArguments()[0]) ) {
                        throw newVerificationException(getDottedPath(),
                                Errors.TRANSITION_CONDITIONAL_CONDITION_NOT_MATCH_JUDGER, clazz, conditionClass, judgerClass);
                    }
                }
            }
        }
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
    public TransitionInst newInstance(Class<?> clazz) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MetaDataFilterable filter(MetaData parent, MetaDataFilter filter, boolean lazyFilter) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isConditional() {
        return conditional;
    }
}
