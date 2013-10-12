package net.madz.test.stochastic.core.impl.processors;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.madz.test.stochastic.core.AbsScriptEngine;
import net.madz.test.stochastic.core.DeduceResultEnum;
import net.madz.test.stochastic.core.DimensionValuePair;
import net.madz.test.stochastic.core.DimensionWeight;
import net.madz.test.stochastic.core.DynamicCaseContext;
import net.madz.test.stochastic.core.GlobalTestContext;
import net.madz.test.stochastic.core.ICombinationHandler;
import net.madz.test.stochastic.core.IConstraint;
import net.madz.test.stochastic.core.IDimension;
import net.madz.test.stochastic.core.IDynamicCase;
import net.madz.test.stochastic.core.IExpectation;
import net.madz.test.stochastic.core.IExploreStrategy;
import net.madz.test.stochastic.core.IFilter;
import net.madz.test.stochastic.core.TestContext;
import net.madz.test.stochastic.core.impl.constraints.ConstraintCombination;
import net.madz.test.stochastic.core.impl.constraints.ExclusionConstraint;
import net.madz.test.stochastic.core.impl.constraints.InclusionConstraint;
import net.madz.test.stochastic.core.report.ExcelFormatTestReporter;
import net.madz.test.stochastic.utilities.annotations.Constraint;
import net.madz.test.stochastic.utilities.annotations.ConstraintDimension;
import net.madz.test.stochastic.utilities.annotations.Filter;
import net.madz.test.stochastic.utilities.annotations.PerCaseCleanup;
import net.madz.test.stochastic.utilities.annotations.PerTestMethodCleanup;
import net.madz.test.stochastic.utilities.annotations.PerThreadCleanup;
import net.madz.test.stochastic.utilities.annotations.Constraint.ConstraintTypeEnum;

public abstract class DynamicCaseProcessor extends AbsScriptEngine<IDynamicCase> {

    private final List<IDimension> dimensions = new ArrayList<IDimension>();
    private volatile boolean headerCreated;
    private volatile int counter;
    private volatile int rowNumber;
    private volatile int totalNumber;

    // TODO [Barry][Code Review] [Encapsulate TestContext from dependent on
    // Spring]
    protected void beforeDynamicCase(TestContext context, DynamicCaseContext dynamicContext) {
    }

    protected void afterDynamicCase(TestContext context, DynamicCaseContext dynamicContext) {
    }

    protected abstract DeduceResultEnum generateExpectation(TestContext context, DynamicCaseContext deduceContext, IDynamicCase t);

    protected abstract void performTestAction(TestContext context, DynamicCaseContext deduceContext);

    @Override
    public void doProcess(final TestContext context, final IDynamicCase dynamicCase) {
        DynamicTestExecutor testExecutor = null;
        ExcelFormatTestReporter reporterCopy = null;
        try {
            this.dimensions.clear();
            this.dimensions.addAll(dynamicCase.getDimensions(context));
            // TODO [Barry][Code Review] [Change IDynamicCase.filters return
            // type to List<IFilter>]
            final ArrayList<IFilter> iFilters = new ArrayList<IFilter>();
            final Filter[] filters = dynamicCase.filters();
            for ( final Filter filter : filters ) {
                iFilters.add(new IFilter() {

                    @Override
                    public String[] includes() {
                        return filter.includes();
                    }

                    @Override
                    public String[] excludes() {
                        return filter.excludes();
                    }

                    @Override
                    public String dimension() {
                        return filter.dimension();
                    }
                });
            }
            final Class<? extends IExploreStrategy> detectorType = dynamicCase.detector();
            final IExploreStrategy explorer = detectorType.newInstance();
            final String fileName = context.getTestClass().getName() + "." + context.getTestMethod().getName();
            reporterCopy = new ExcelFormatTestReporter(fileName);
            final ExcelFormatTestReporter reporter = reporterCopy;
            resetCounters();
            explorer.setCombinationHandler(new CombinationCounter());
            final Map<String, Integer> dimensionIndex = summerizeConstraints(dynamicCase.constraints());
            final ConstraintCombination constraintCombination = parseConstraints(dynamicCase, dimensionIndex);
            explorer.doExplore(dimensions, iFilters.toArray(new IFilter[0]), constraintCombination, dimensionIndex);
            if ( getTotalNumber() > dynamicCase.maxCombinations() ) {
                throw new IllegalArgumentException("Combination number exceeded limitation.Please add constraints or remove useless dimensions. "
                        + "Combination Number : " + getTotalNumber() + ", Limitation: " + dynamicCase.maxCombinations());
            } else {
                debug("Found combinations: " + getTotalNumber());
            }
            testExecutor = new DynamicTestExecutor(this, reporter, context, dynamicCase);
            explorer.setCombinationHandler(testExecutor);
            explorer.doExplore(dimensions, iFilters.toArray(new IFilter[0]), constraintCombination, dimensionIndex);
            synchronized (this) {
                while ( this.totalNumber - this.counter > 0 ) {
                    wait();
                }
            }
        } catch (Throwable ex) {
            // TODO [Barry] [How to handle such exception?]
            final IllegalStateException wrapper = new IllegalStateException(ex);
            throw wrapper;
        } finally {
            // When @Currency were not annotated or @Currency(threads=1),
            // dynamic test was expected run in main thread.
            try {
                cleanupResourcePerThread(context);
            } catch (Throwable ignore) {
            }
            if ( null != reporterCopy ) {
                try {
                    reporterCopy.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                resetCounters();
            } catch (Throwable ignore) {
            }
            if ( null != testExecutor ) {
                try {
                    testExecutor.shutdown();
                } catch (Throwable ignore) {
                }
            }
            try {
                cleanupResourcePerTestMethod(context);
            } catch (Throwable ignore) {
            }
        }
    }

    private void cleanupResourcePerTestMethod(TestContext context) {
        doCleanup(context, PerTestMethodCleanup.class);
    }

    public void cleanupResourcePerThread(TestContext context) {
        doCleanup(context, PerThreadCleanup.class);
        GlobalTestContext.getInstance().clearLocalFunctions();
        GlobalTestContext.getInstance().clearLocalVariables();
    }

    public <T extends Annotation> void doCleanup(TestContext context, Class<T> cleanupAnnotationClass) {
        final Annotation[] annotations = context.getTestMethod().getAnnotations();
        for ( Annotation annotation : annotations ) {
            T cleanup = annotation.annotationType().getAnnotation(cleanupAnnotationClass);
            if ( null != cleanup ) {
                try {
                    executeScript(context, annotation);
                } catch (Throwable ignored) {
                }
            }
        }
    }

    public void cleanupResourcePerCase(TestContext context) {
        doCleanup(context, PerCaseCleanup.class);
    }

    public ConstraintCombination parseConstraints(final IDynamicCase t, Map<String, Integer> dimensionIndex) {
        final ArrayList<IConstraint> inclusions = new ArrayList<IConstraint>();
        final ArrayList<IConstraint> exclusions = new ArrayList<IConstraint>();
        for ( Constraint constraint : t.constraints() ) {
            ConstraintTypeEnum type = constraint.type();
            if ( ConstraintTypeEnum.Inclusion == type ) {
                IConstraint e = createConstraint(constraint, dimensionIndex);
                inclusions.add(e);
            } else {
                IConstraint e = createConstraint(constraint, dimensionIndex);
                exclusions.add(e);
            }
        }
        final ConstraintCombination constraintCombination = new ConstraintCombination(inclusions.toArray(new IConstraint[0]),
                exclusions.toArray(new IConstraint[0]));
        return constraintCombination;
    }

    private IConstraint createConstraint(Constraint constraint, final Map<String, Integer> dimensionIndex) {
        ConstraintTypeEnum type = constraint.type();
        final String[] constraintSequence = new String[dimensionIndex.size()];
        final ConstraintDimension[] combination = constraint.combination();
        for ( ConstraintDimension constraintDimension : combination ) {
            Integer index = dimensionIndex.get(constraintDimension.name());
            constraintSequence[index] = constraintDimension.value();
        }
        if ( ConstraintTypeEnum.Inclusion == type ) {
            for ( int i = 0; i < constraintSequence.length; i++ ) {
                if ( null == constraintSequence[i] ) {
                    constraintSequence[i] = IConstraint.INCLUSION_PLACEHOLDER;
                }
            }
            return new InclusionConstraint(constraintSequence);
        } else {
            for ( int i = 0; i < constraintSequence.length; i++ ) {
                if ( null == constraintSequence[i] ) {
                    constraintSequence[i] = IConstraint.EXCLUSION_PLACEHOLDER;
                }
            }
            return new ExclusionConstraint(constraintSequence);
        }
    }

    private Map<String, Integer> summerizeConstraints(Constraint[] constraints) {
        final HashMap<String, DimensionWeight> weightsMap = new HashMap<String, DimensionWeight>();
        for ( IDimension dimension : this.dimensions ) {
            weightsMap.put(dimension.getDottedName(), new DimensionWeight(dimension.getDottedName(), 0));
        }
        for ( Constraint constraint : constraints ) {
            ConstraintDimension[] combination = constraint.combination();
            for ( ConstraintDimension constraintDimension : combination ) {
                String dimensionAlias = constraintDimension.name();
                if ( weightsMap.containsKey(dimensionAlias) ) {
                    weightsMap.get(dimensionAlias).increment();
                } else {
                    throw new IllegalStateException("Illegal Dimension: " + dimensionAlias);
                }
            }
        }
        final ArrayList<DimensionWeight> weights = new ArrayList<DimensionWeight>(weightsMap.values());
        Collections.sort(weights);
        final HashMap<String, Integer> index = new HashMap<String, Integer>();
        for ( int i = 0; i < weights.size(); i++ ) {
            index.put(weights.get(i).getDottedName(), i);
        }
        return Collections.unmodifiableMap(index);
    }

    private final class CombinationCounter implements ICombinationHandler {

        @Override
        public void onCombinationFound(final DynamicCaseContext dynamicTestContext) {
            synchronized (DynamicCaseProcessor.this) {
                setTotalNumber(getTotalNumber() + 1);
            }
        }
    }

    public void resetCounters() {
        counter = 0;
        setHeaderCreated(false);
        rowNumber = 2;
        setTotalNumber(0);
    }

    void produceTargetState(TestContext context, DynamicCaseContext deduceContext) {
        System.out.println("Producing Target States...");
        final List<DimensionValuePair> targetStateList = deduceContext.getTargetStateList();
        for ( DimensionValuePair targetState : targetStateList ) {
            targetState.getDimension().choose(context, targetState.getChoice());
        }
    }

    void verifyExpectations(TestContext context, DynamicCaseContext deduceContext) {
        System.out.println("Verifying Expectations...");
        final List<IExpectation> expectations = deduceContext.getExpectations();
        for ( IExpectation iExpectation : expectations ) {
            iExpectation.verify(deduceContext);
        }
    }

    void setTotalNumber(int totalNumber) {
        this.totalNumber = totalNumber;
    }

    int getTotalNumber() {
        return totalNumber;
    }

    void setHeaderCreated(boolean headerCreated) {
        this.headerCreated = headerCreated;
    }

    boolean isHeaderCreated() {
        return headerCreated;
    }

    public void incrementCounter() {
        synchronized (this) {
            this.counter++;
            notify();
        }
    }

    public synchronized int incrementRowNumber() {
        return rowNumber++;
    }
}
