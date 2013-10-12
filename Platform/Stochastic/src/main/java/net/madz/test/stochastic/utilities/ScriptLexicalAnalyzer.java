package net.madz.test.stochastic.utilities;

import net.madz.test.stochastic.core.GlobalTestContext;

public class ScriptLexicalAnalyzer {

    public static String stripVariablePlaceholder(final String with) {
        final String navigateExpression = with.replace("${", "").replace("}", "");
        return navigateExpression;
    }

    public static Object evaluateVariableExpression(final String with) {
        final Object withValue;
        if ( with.startsWith("${") ) {
            final String navigateExpression = stripVariablePlaceholder(with);
            withValue = GlobalTestContext.getInstance().evaluateVariableNavigation(navigateExpression);
        } else if ( "null".equalsIgnoreCase(with) ) {
            withValue = null;
        } else {
            withValue = with;
        }
        return withValue;
    }

    public static String preprocessNavigationExpression(String with) {
        if ( null == with || 0 >= with.trim().length() ) {
            throw new IllegalArgumentException("Argument with is empty or null.");
        }
        int first = with.indexOf('.');
        int last = with.lastIndexOf('.');
        if ( first == last && 1 >= with.length() ) {
            throw new IllegalArgumentException("Argument with is illegal: with = " + with);
        }
        if ( last == with.length() - 1 ) {
            with = with.substring(0, last);
        }
        if ( first == 0 ) {
            with = with.substring(1);
        }
        return with;
    }

    public static Object evaluateVariableNavigation(Object variableInstance, String navigation) {
        /*
         * if (variableInstance instanceof SObject) { return
         * SfdcLexicalAnalyzer.evaluateSObjectProperty((SObject)
         * variableInstance, navigation); }
         */
        if ( 0 > navigation.indexOf('.') ) {
            return ClassUtils.getProperty(variableInstance, navigation);
        } else {
            final int first = navigation.indexOf('.');
            return evaluateVariableNavigation(ClassUtils.getProperty(variableInstance, navigation.substring(0, first)), navigation.substring(first + 1));
        }
    }
}
