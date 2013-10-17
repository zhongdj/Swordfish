package net.madz.test.rest.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import net.madz.test.rest.HttpUnitRunner;
import net.madz.test.rest.InternalHttpUnitRunner;
import net.madz.test.rest.VariableContext;
import net.madz.test.stochastic.core.AbsScriptEngine;
import net.madz.test.stochastic.core.TestContext;
import net.madz.test.stochastic.utilities.annotations.Processor;

import org.junit.rules.TestRule;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.TestClass;

/**
 * The @Create is a test script construct that describes a flexible RESTful POST
 * request.<br/>
 * The flexibility includes:<br/>
 * 1. To merge context variables into POST URI with @UriParam.<br/>
 * 2. To merge context variables into POST Entity(the JSON/XML content)
 * with @MergeField.<br/>
 * 3. To extract context variables from POST Response into Context with
 * 
 * @Extractor.<br/>
 * 
 * @author Tracy Lu
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Processor(Create.Processor.class)
public @interface Create {

    /**
     * @return RESTful Action Class with RESTful POST request metadata, such
     *         as:<br/>
     *         URI, HTTP Entity, Headers, Authorization(implicitly).
     * 
     */
    Class<?> action();

    /**
     * @return @Extrator array describes relations mapping from RESTful POST
     *         Response expressions to context variables.<br/>
     */
    Extractor[] extractors();

    /**
     * @return @MergeField array describes merging metadata mapping from context
     *         variables to RESTFul POST request entity template merge fields.
     */
    MergeField[] mergeFields() default {};

    /**
     * @return @UriParam array describes merging metadata mapping from context
     *         variables to RESTFul POST request URI parameters.
     */
    UriParam[] uriParams() default {};

    public static class Processor extends AbsScriptEngine<Create> {

        @Override
        public void doProcess(final TestContext context, Create t) throws Throwable {
            VariableContext.getInstance().pushScope(null == context.getTarget());
            VariableContext.getInstance().setVariableBindings(t.uriParams(), t.mergeFields(), t.extractors());
            try {
                InternalHttpUnitRunner runner = new InternalHttpUnitRunner(context, t.action());
                runner.run(new RunNotifier());
            } finally {
                VariableContext.getInstance().clearVariableBindings();
                VariableContext.getInstance().popScope();
            }
        }
    }
}
