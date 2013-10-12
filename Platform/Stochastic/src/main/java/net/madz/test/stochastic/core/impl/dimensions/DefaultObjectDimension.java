package net.madz.test.stochastic.core.impl.dimensions;

import net.madz.test.stochastic.core.GlobalTestContext;
import net.madz.test.stochastic.core.IObjectDimension;
import net.madz.test.stochastic.core.TestContext;
import net.madz.test.stochastic.utilities.ScriptLexicalAnalyzer;

public class DefaultObjectDimension extends DefaultDimension implements IObjectDimension {

    protected String oneExpression;

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
    public void choose(TestContext context, String choice) {
        final Object one = getOne();
        if ( null == one ) {
            throw new NullPointerException("Please set Object type value to 'one' first.");
        }
        try {
            GlobalTestContext.getInstance().registerLocalVariable("objectDimensionInstance", one);
            processAnnotations(context, choice);
        } finally {
            GlobalTestContext.getInstance().removeLocalVariable("objectDimensionInstance");
        }
    }

    @Override
    public String getDottedName() {
        return ScriptLexicalAnalyzer.stripVariablePlaceholder(getOneExpression()) + "." + getAlias();
    }
}
