package net.madz.lifecycle.meta.builder.impl.helpers;

import java.lang.reflect.Method;

import net.madz.lifecycle.annotations.LifecycleMeta;
import net.madz.lifecycle.annotations.relation.Relation;
import net.madz.lifecycle.meta.builder.impl.RelationObjectBuilderImpl;
import net.madz.lifecycle.meta.builder.impl.StateMachineObjectBuilderImpl;
import net.madz.lifecycle.meta.object.RelationObject;
import net.madz.lifecycle.meta.object.StateMachineObject;
import net.madz.lifecycle.meta.type.RelationMetadata;
import net.madz.lifecycle.meta.type.StateMachineMetadata;
import net.madz.util.MethodScanCallback;
import net.madz.util.StringUtil;
import net.madz.utils.Null;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailureSet;

public final class RelationGetterConfigureScanner implements MethodScanCallback {

    /**
     * 
     */
    private final StateMachineObjectBuilderImpl<?> stateMachineObjectBuilderImpl;
    final private StateMachineObject<?> stateMachineObject;
    private final VerificationFailureSet failureSet;
    private final Class<?> klass;

    public RelationGetterConfigureScanner(Class<?> klass, StateMachineObjectBuilderImpl<?> stateMachineObjectBuilderImpl,
            StateMachineObject<?> stateMachineObject, VerificationFailureSet failureSet) {
        super();
        this.klass = klass;
        this.stateMachineObjectBuilderImpl = stateMachineObjectBuilderImpl;
        this.stateMachineObject = stateMachineObject;
        this.failureSet = failureSet;
    }

    @Override
    public boolean onMethodFound(Method method) {
        Relation relation = method.getAnnotation(Relation.class);
        if ( null != relation ) {
            RelationObject relationObject = null;
            StateMachineMetadata relatedStateMachine = null;
            try {
                relatedStateMachine = this.stateMachineObjectBuilderImpl.getMetaType().getRegistry()
                        .loadStateMachineMetadata(method.getDeclaringClass().getAnnotation(LifecycleMeta.class).value());
                final RelationMetadata relationMetadata;
                if ( Null.class == relation.value() ) {
                    if ( method.getName().startsWith("get") ) {
                        relationMetadata = relatedStateMachine.getRelationMetadata(StringUtil.toUppercaseFirstCharacter(method.getName().substring(3)));
                    } else {
                        relationMetadata = relatedStateMachine.getRelationMetadata(StringUtil.toUppercaseFirstCharacter(method.getName()));
                    }
                } else {
                    relationMetadata = relatedStateMachine.getRelationMetadata(relation.value());
                }
                relationObject = new RelationObjectBuilderImpl(stateMachineObject, method, relationMetadata).build(klass, stateMachineObject).getMetaData();
                this.stateMachineObjectBuilderImpl.addRelation(method.getDeclaringClass(), relationObject, relationMetadata.getPrimaryKey());
            } catch (VerificationException e) {
                failureSet.add(e);
            }
        }
        return false;
    }
}