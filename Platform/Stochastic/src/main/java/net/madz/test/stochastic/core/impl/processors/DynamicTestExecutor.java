package net.madz.test.stochastic.core.impl.processors;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import net.madz.test.stochastic.core.DeduceResultEnum;
import net.madz.test.stochastic.core.DynamicCaseContext;
import net.madz.test.stochastic.core.ICombinationHandler;
import net.madz.test.stochastic.core.IDynamicCase;
import net.madz.test.stochastic.core.TestContext;
import net.madz.test.stochastic.core.report.ExcelFormatTestReporter;
import net.madz.test.stochastic.utilities.annotations.Concurrency;

final class DynamicTestExecutor implements ICombinationHandler {

    private final DynamicCaseProcessor dynamicCaseProcessor;
    private final ExcelFormatTestReporter reporter;
    private final TestContext context;
    private final IDynamicCase dynamicCase;
    private volatile int currentCaseNumber = 0;
    private final ExecutorService executor;
    private final int threads;

    DynamicTestExecutor(final DynamicCaseProcessor dynamicCaseProcessor, ExcelFormatTestReporter reporter, final TestContext context,
            final IDynamicCase dynamicCase) {
        this.dynamicCaseProcessor = dynamicCaseProcessor;
        this.reporter = reporter;
        this.context = context;
        this.dynamicCase = dynamicCase;
        final Concurrency concurrency = context.getTestMethod().getAnnotation(Concurrency.class);
        if ( null != concurrency ) {
            threads = concurrency.threads();
        } else {
            threads = 1;
        }
        executor = Executors.newFixedThreadPool(threads, new ThreadFactory() {

            private int counter = 0;

            @Override
            public Thread newThread(Runnable r) {
                return new DynamicTestThread(++counter, r);
            }
        });
    }

    @Override
    public void onCombinationFound(final DynamicCaseContext dynamicTestContext) {
        dynamicTestContext.setCaseName(this.context.getTestMethod().getName());
        final DynamicTestRunnable runnable = new DynamicTestRunnable(context, dynamicCaseProcessor, this, dynamicTestContext);
        executor.submit(runnable);
    }

    public void runTest(final DynamicCaseContext dynamicTestContext) {
        this.dynamicCaseProcessor.beforeDynamicCase(context, dynamicTestContext);
        try {
            synchronized (dynamicCaseProcessor) {
                if ( !this.dynamicCaseProcessor.isHeaderCreated() ) {
                    reporter.createWorksheet(dynamicTestContext.getCaseName(), dynamicTestContext.getReportHeader());
                    this.dynamicCaseProcessor.setHeaderCreated(true);
                }
            }
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        DynamicCaseProcessor.debug("Processing case No." + ( ++currentCaseNumber ) + "/" + this.dynamicCaseProcessor.getTotalNumber());
        if ( DeduceResultEnum.Skip == this.dynamicCaseProcessor.generateExpectation(context, dynamicTestContext, dynamicCase) ) {
            dynamicTestContext.debug();
            dynamicTestContext.skip();
            return;
        }
        dynamicTestContext.debug();
        long start = System.currentTimeMillis();
        this.dynamicCaseProcessor.produceTargetState(context, dynamicTestContext);
        System.out.println("produce target state cost: " + ( System.currentTimeMillis() - start ) + "ms");
        start = System.currentTimeMillis();
        this.dynamicCaseProcessor.performTestAction(context, dynamicTestContext);
        System.out.println("sync cost: " + ( System.currentTimeMillis() - start ) + "ms");
        start = System.currentTimeMillis();
        this.dynamicCaseProcessor.verifyExpectations(context, dynamicTestContext);
        System.out.println("verify cost: " + ( System.currentTimeMillis() - start ) + "ms");
    }

    public void logResult(final DynamicCaseContext dynamicTestContext) {
        try {
            reporter.createRow(dynamicTestContext.getReportRow(this.dynamicCaseProcessor.incrementRowNumber()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        if ( null != executor ) {
            final CountDownLatch beginLatch = new CountDownLatch(threads);
            final CountDownLatch finishLatch = new CountDownLatch(threads);
            for ( int i = 0; i < threads; i++ ) {
                executor.submit(new Runnable() {

                    @Override
                    public void run() {
                        beginLatch.countDown();
                        try {
                            beginLatch.await();
                        } catch (InterruptedException e) {
                        }
                        try {
                            dynamicCaseProcessor.cleanupResourcePerThread(context);
                        } catch (Throwable ignore) {
                        } finally {
                            finishLatch.countDown();
                        }
                    }
                });
            }
            try {
                finishLatch.await();
            } catch (InterruptedException e) {
            }
            executor.shutdownNow();
        }
    }
}