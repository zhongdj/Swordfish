package net.madz.test.stochastic.core.impl.processors;

import java.lang.annotation.Annotation;

import net.madz.test.stochastic.core.DynamicCaseContext;
import net.madz.test.stochastic.core.TestContext;
import net.madz.test.stochastic.utilities.annotations.PerCaseSetup;
import net.madz.test.stochastic.utilities.annotations.PerThreadSetup;

public class DynamicTestRunnable implements Runnable {

    private final static ThreadLocal<Boolean> resourceSetup = new ThreadLocal<Boolean>();
    private final DynamicTestExecutor dynamicTestExecutor;
    private final DynamicCaseContext dynamicTestContext;
    private final DynamicCaseProcessor dynamicTestProcessor;
    private final TestContext context;

    public DynamicTestRunnable(final TestContext context, final DynamicCaseProcessor dynamicCaseProcessor, final DynamicTestExecutor dynamicTestExecutor,
            DynamicCaseContext dynamicTestContext) {
        this.context = context;
        this.dynamicTestProcessor = dynamicCaseProcessor;
        this.dynamicTestExecutor = dynamicTestExecutor;
        this.dynamicTestContext = dynamicTestContext;
    }

    public void run() {
        try {
            doResourceSetup();
            doRunTest();
        } catch (Throwable e) {
            // TODO [Barry][Code Review] [Add exception verification]
            e.printStackTrace();
            dynamicTestContext.fail(e);
        } finally {
            try {
                dynamicTestProcessor.incrementCounter();
            } catch (Throwable ignore) {
            }
            try {
                dynamicTestContext.report();
            } catch (Throwable ignore) {
            }
            try {
                dynamicTestExecutor.logResult(dynamicTestContext);
            } catch (Throwable ignore) {
            }
            try {
                dynamicTestProcessor.afterDynamicCase(context, dynamicTestContext);
            } catch (Throwable ignore) {
            }
            try {
                dynamicTestProcessor.cleanupResourcePerCase(context);
            } catch (Throwable ignore) {
            }
        }
    }

    private void doRunTest() {
        // t.doExecute(new OpenSessionCallback() {
        //
        // @Override
        // public void execute() {
        final Annotation[] annotations = context.getTestMethod().getAnnotations();
        for ( Annotation annotation : annotations ) {
            final PerCaseSetup perCase = annotation.annotationType().getAnnotation(PerCaseSetup.class);
            if ( null != perCase ) {
                dynamicTestProcessor.executeScript(context, annotation);
            }
        }
        dynamicTestExecutor.runTest(dynamicTestContext);
        dynamicTestContext.pass();
        // }
        // });
    }

    private void doResourceSetup() {
        if ( null == resourceSetup.get() || !resourceSetup.get() ) {
            // t.doExecute(new OpenSessionCallback() {
            //
            // @Override
            // public void execute() {
            final Annotation[] annotations = context.getTestMethod().getAnnotations();
            for ( Annotation annotation : annotations ) {
                final PerThreadSetup perThread = annotation.annotationType().getAnnotation(PerThreadSetup.class);
                if ( null != perThread ) {
                    dynamicTestProcessor.executeScript(context, annotation);
                }
            }
        }
        // });
        resourceSetup.set(true);
    }
}
