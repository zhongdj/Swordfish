package net.madz.test.stochastic.core;

import java.lang.annotation.Annotation;
import java.util.logging.Logger;

import net.madz.test.stochastic.utilities.ScriptLexicalAnalyzer;
import net.madz.test.stochastic.utilities.annotations.Processor;

public abstract class AbsScriptEngine<META> implements ScriptEngine<META> {

    private static Logger logger = Logger.getLogger(AbsScriptEngine.class.getName());

    public void doProcess(TestContext context, META t) throws Throwable {
        context.getBase().evaluate();
    }

    public void doProcess(TestContext context, META[] ts) throws Throwable {
        for ( META t : ts ) {
            doProcess(context, t);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void executeScript(TestContext context, final Annotation script) throws Throwable {
        increaseIndent();
        debug(script.toString());
        try {
            if ( null != script ) {
                final Processor processor = script.annotationType().getAnnotation(Processor.class);
                if ( null == processor ) {
                    throw new NullPointerException("Cannot find Processor for Annotation: "
                            + script.annotationType().getName());
                }
                final Class<? extends AbsScriptEngine> processorClass = (Class<? extends AbsScriptEngine>) processor
                        .value();
                final AbsScriptEngine processInstance = processorClass.newInstance();
                processInstance.doProcess(context, script);
            }
        } finally {
            decreaseIndent();
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void processAnnotation(TestContext context, final Annotation[] annotations) throws Throwable {
        try {
            increaseIndent();
            if ( null != annotations && 0 < annotations.length ) {
                debug(annotations);
                final Annotation annotation = annotations[0];
                final Processor processor = annotation.annotationType().getAnnotation(Processor.class);
                if ( null == processor ) {
                    throw new NullPointerException("Cannot find Processor for Annotation: "
                            + annotation.annotationType().getName());
                }
                final Class<? extends AbsScriptEngine> processorClass = (Class<? extends AbsScriptEngine>) processor
                        .value();
                final AbsScriptEngine processInstance = processorClass.newInstance();
                processInstance.doProcess(context, annotations);
            }
        } finally {
            decreaseIndent();
        }
    }

    protected static void debug(Annotation[] annotations) {
        for ( Annotation annotation : annotations ) {
            debug(annotation.toString());
        }
    }

    public static void debug(String message) {
        final StringBuilder spaceBuilder = new StringBuilder();
        for ( int i = 0; i < getIndent(); i++ ) {
            spaceBuilder.append("  ");
        }
        final String indentString = spaceBuilder.toString() + "  ";
        final StringBuilder builder = new StringBuilder();
        builder.append(message);
        logger.info(indentString + builder.toString());
    }

    public static Integer getIndent() {
        return GlobalTestContext.getInstance().getIndent();
    }

    public static void decreaseIndent() {
        GlobalTestContext.getInstance().decreaseIndent();
    }

    public static void increaseIndent() {
        GlobalTestContext.getInstance().increaseIndent();
    }

    public Object evaluate(final String with) {
        final Object withValue;
        withValue = ScriptLexicalAnalyzer.evaluateVariableExpression(with);
        increaseIndent();
        debug("evaluating: name = " + with + ", value = " + ( withValue == null ? "null" : withValue ));
        decreaseIndent();
        return withValue;
    }
}