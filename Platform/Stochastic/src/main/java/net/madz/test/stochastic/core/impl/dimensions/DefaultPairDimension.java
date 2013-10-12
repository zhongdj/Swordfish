package net.madz.test.stochastic.core.impl.dimensions;

import net.madz.test.stochastic.core.GlobalTestContext;
import net.madz.test.stochastic.core.IPairDimension;
import net.madz.test.stochastic.core.TestContext;
import net.madz.test.stochastic.utilities.ScriptLexicalAnalyzer;

public class DefaultPairDimension extends DefaultDimension implements IPairDimension {

    protected String oneExpression;
    protected String otherExpression;

    public void setOne(String tExpression) {
        this.oneExpression = tExpression;
    }

    public String getOneExpression() {
        return oneExpression;
    }

    public Object getOne() {
        return evaluate(oneExpression);
    }

    @Override
    public String getOtherExpression() {
        return otherExpression;
    }

    @Override
    public void setOther(String otherExpression) {
        this.otherExpression = otherExpression;
    }

    @Override
    public String getAlias() {
        return ScriptLexicalAnalyzer.stripVariablePlaceholder(oneExpression) + "." + alias;
    }

    @Override
    public Object getOther() {
        return evaluate(otherExpression);
    }

    @Override
    public void choose(TestContext context, String choice) {
        final Object one = getOne();
        final Object other = getOther();
        if ( null == one || null == other ) {
            throw new NullPointerException("Please set objects pair first.");
        }
        try {
            GlobalTestContext.getInstance().registerLocalVariable("pairDimensionOne", one);
            GlobalTestContext.getInstance().registerLocalVariable("pairDimensionOther", other);
            processAnnotations(context, choice);
        } finally {
            GlobalTestContext.getInstance().removeLocalVariable("pairDimensionOne");
            GlobalTestContext.getInstance().removeLocalVariable("pairDimensionOther");
        }
    }

    @Override
    public String getDottedName() {
        return ScriptLexicalAnalyzer.stripVariablePlaceholder(getAlias());
    }
}
