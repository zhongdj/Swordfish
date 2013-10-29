package net.madz.lifecycle.syntax;

import net.madz.lifecycle.Errors;
import net.madz.lifecycle.meta.builder.StateMachineMetaBuilder;
import net.madz.utils.BundleUtils;

public class BaseMetaDataTest {

    public BaseMetaDataTest() {
        super();
    }

    protected String getMessage(String errorCode, Object[] args) {
        return BundleUtils.getBundledMessage(StateMachineMetaBuilder.class, Errors.SYNTAX_ERROR_BUNDLE, errorCode, args);
    }
}