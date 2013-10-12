package net.madz.test;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import net.madz.test.stochastic.core.AbsScriptEngine;
import net.madz.test.stochastic.core.TestContext;
import net.madz.test.stochastic.core.impl.DefaultTestContext;
import net.madz.test.stochastic.utilities.annotations.Processor;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class MadzTestRunner extends BlockJUnit4ClassRunner {

    private Object target;

    public MadzTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected Object createTest() throws Exception {
        target = super.createTest();
        return target;
    }

    @Override
    protected Statement classBlock(RunNotifier notifier) {
        Statement result = super.classBlock(notifier);
        result = withClassProcessables(result);
        return result;
    }

    @Override
    protected Statement methodBlock(FrameworkMethod method) {
        Statement result = super.methodBlock(method);
        result = withMethodProcessables(result, method, target);
        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Statement withMethodProcessables(Statement result, FrameworkMethod method, Object object) {
        Annotation[] annotations = method.getAnnotations();
        for ( int i = annotations.length - 1; i >= 0; i-- ) {
            if ( null != annotations[i].annotationType().getAnnotation(Processor.class) ) {
                result = new ScriptStatement(result, annotations[i], method.getMethod(), object);
            }
        }
        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Statement withClassProcessables(Statement result) {
        Annotation[] annotations = getTestClass().getAnnotations();
        for ( int i = annotations.length - 1; i >= 0; i-- ) {
            if ( null != annotations[i].annotationType().getAnnotation(Processor.class) ) {
                result = new ScriptStatement(result, annotations[i], getTestClass().getJavaClass(), null);
            }
        }
        return result;
    }

    private class ScriptStatement<META extends Annotation, P extends AbsScriptEngine<META>> extends Statement {

        private final META script;

        private final AnnotatedElement container;

        private final Object target;

        private final Statement base;

        public ScriptStatement(Statement base, META script, AnnotatedElement container, Object target) {
            super();
            this.base = base;
            this.script = script;
            this.container = container;
            this.target = target;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void evaluate() throws Throwable {
            final Processor metaProcessor = script.annotationType().getAnnotation(Processor.class);
            final AbsScriptEngine<META> processor = (AbsScriptEngine<META>) metaProcessor.value().newInstance();
            Method method = null;
            if ( container instanceof Method ) {
                method = (Method) container;
            }
            final TestContext context = new DefaultTestContext(base, getTestClass().getJavaClass(), method, target);
            try {
                processor.executeScript(context, script);
            } finally {
            }
        }
    }
}
