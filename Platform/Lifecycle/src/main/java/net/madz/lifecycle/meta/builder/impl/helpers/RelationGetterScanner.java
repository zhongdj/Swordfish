package net.madz.lifecycle.meta.builder.impl.helpers;

import java.lang.reflect.Method;

import net.madz.lifecycle.annotations.relation.Relation;
import net.madz.lifecycle.meta.builder.impl.StateMachineObjectBuilderImpl;
import net.madz.lifecycle.meta.type.RelationConstraintMetadata;
import net.madz.util.MethodScanCallback;
import net.madz.utils.Null;

public final class RelationGetterScanner implements MethodScanCallback {

    private final StateMachineObjectBuilderImpl<?> stateMachineObjectBuilderImpl;
    private RelationConstraintMetadata relationMetadata;

    public RelationGetterScanner(StateMachineObjectBuilderImpl<?> stateMachineObjectBuilderImpl, RelationConstraintMetadata relation) {
        this.stateMachineObjectBuilderImpl = stateMachineObjectBuilderImpl;
        this.relationMetadata = relation;
    }

    public boolean covered = false;

    @Override
    public boolean onMethodFound(Method method) {
        if ( method.getName().startsWith("get") ) {
            Relation relation = method.getAnnotation(Relation.class);
            if ( null != relation ) {
                if ( Null.class == relation.value() ) {
                    if ( this.stateMachineObjectBuilderImpl.isKeyOfRelationMetadata(relationMetadata, method.getName().substring(3)) ) {
                        covered = true;
                        return true;
                    }
                } else {
                    if ( this.stateMachineObjectBuilderImpl.isKeyOfRelationMetadata(relationMetadata, relation.value()) ) {
                        covered = true;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isCovered() {
        return covered;
    }
}