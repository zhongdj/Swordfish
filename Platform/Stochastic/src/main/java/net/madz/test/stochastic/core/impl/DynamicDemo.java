package net.madz.test.stochastic.core.impl;

import java.lang.reflect.Method;

import net.madz.stochastic.demo.dimensions.FirstDim;
import net.madz.stochastic.demo.dimensions.SecondDim;
import net.madz.test.stochastic.core.DeduceResultEnum;
import net.madz.test.stochastic.core.DynamicCaseContext;
import net.madz.test.stochastic.core.IDynamicCase;
import net.madz.test.stochastic.core.IExpectation;
import net.madz.test.stochastic.core.TestContext;
import net.madz.test.stochastic.core.impl.processors.DynamicCaseProcessor;
import net.madz.test.stochastic.utilities.annotations.Dimension;
import net.madz.test.stochastic.utilities.annotations.DynamicCase;

public class DynamicDemo {

    public static void main(String[] args) {
        final Method[] declaredMethods = DynamicDemo.class.getDeclaredMethods();
        for ( final Method m : declaredMethods ) {
            if ( m.getAnnotation(DynamicCase.class) == null ) {
                continue;
            }
            DynamicCase dynamicCase = m.getAnnotation(DynamicCase.class);
            new Processor().doProcess(new DefaultTestContext(null, DynamicDemo.class, m, null),
                    new GlobalDimensionBasedDynamicCase(dynamicCase));
        }
    }

    private static final class Processor extends DynamicCaseProcessor {

        @Override
        protected DeduceResultEnum generateExpectation(TestContext context, DynamicCaseContext deduceContext,
                IDynamicCase t) {
            deduceContext.addExpectation(new IExpectation() {

                @Override
                public void verify(DynamicCaseContext context) {
                    System.out.println("------------Always Passed.----------");
                }

                @Override
                public boolean isNegative() {
                    return false;
                }

                @Override
                public String getFormalizedString() {
                    return "Always pass expectation!!!!!!!!!!";
                }

                public String toString() {
                    return "1st expectation: always passing. ";
                }
            });
            return DeduceResultEnum.Pass;
        }

        @Override
        protected void performTestAction(TestContext context, DynamicCaseContext deduceContext) {
        }
    }

    @DynamicCase(dimensions = { @Dimension(alias = "1st", enumClass = FirstDim.class, priority = 3),
            @Dimension(alias = "2nd", enumClass = SecondDim.class, priority = 2) })
    public void test() {
    }
}
