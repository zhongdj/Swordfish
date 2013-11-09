package net.madz.lifecycle.meta.impl.builder;

import net.madz.common.DottedPath;
import net.madz.lifecycle.AbsStateMachineRegistry;
import net.madz.lifecycle.SyntaxErrors;
import net.madz.lifecycle.meta.builder.AnnotationMetaBuilder;
import net.madz.meta.MetaData;
import net.madz.meta.impl.MetaDataBuilderBase;
import net.madz.utils.BundleUtils;
import net.madz.verification.VerificationException;
import net.madz.verification.VerificationFailure;

public abstract class AnnotationMetaBuilderBase<SELF extends MetaData, PARENT extends MetaData> extends
        MetaDataBuilderBase<SELF, PARENT> implements AnnotationMetaBuilder<SELF, PARENT> {

    protected AbsStateMachineRegistry registry;

    public AbsStateMachineRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(AbsStateMachineRegistry registry) {
        this.registry = registry;
    }

    protected VerificationException newVerificationException(String dottedPathName, String errorCode, Object... args) {
        return new VerificationException(newVerificationFailure(dottedPathName, errorCode, args));
    }

    protected VerificationException newVerificationException(DottedPath dottedPath, String errorCode, Object... args) {
        return new VerificationException(newVerificationFailure(dottedPath.getAbsoluteName(), errorCode, args));
    }

    protected VerificationFailure newVerificationFailure(DottedPath dottedPath, String errorCode, Object... args) {
        return newVerificationFailure(dottedPath.getAbsoluteName(), errorCode, args);
    }

    protected VerificationFailure newVerificationFailure(String dottedPathName, String errorCode, Object... args) {
        return new VerificationFailure(this, dottedPathName, errorCode, BundleUtils.getBundledMessage(getClass(),
                SyntaxErrors.SYNTAX_ERROR_BUNDLE, errorCode, args));
    }

    protected void addKeys(Class<?> clazz) {
        addKey(getDottedPath());
        addKey(getDottedPath().getAbsoluteName());
        addKey(clazz);
        addKey(clazz.getName());
        addKey(clazz.getSimpleName());
    }

    protected AnnotationMetaBuilderBase(PARENT parent, String name) {
        super(parent, name);
        System.out.println(getDottedPath().getAbsoluteName());
    }
}