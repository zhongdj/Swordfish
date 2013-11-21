package net.madz.lifecycle.meta.impl.builder;

import java.lang.reflect.Method;

import net.madz.lifecycle.meta.MetaObject.ReadAccessor;

public class RelationalCallbackObject extends CallbackObject {

    private final ReadAccessor<?> readAccessor;

    public RelationalCallbackObject(String fromStateName, String toStateName, Method callbackMethod, ReadAccessor<?> accessor) {
        super(fromStateName, toStateName, callbackMethod);
        this.readAccessor = accessor;
    }

    @Override
    protected Object evaluateTarget(Object target) {
        return this.readAccessor.read(target);
    }
}
