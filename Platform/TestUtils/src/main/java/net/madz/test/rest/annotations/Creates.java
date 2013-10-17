package net.madz.test.rest.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.madz.test.stochastic.core.AbsScriptEngine;
import net.madz.test.stochastic.core.TestContext;
import net.madz.test.stochastic.utilities.annotations.Processor;

/**
 * The @Creates is a test script construct that defines a set of @Create
 * 
 * @author Tracy Lu
 * 
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Processor(Creates.Processor.class)
public @interface Creates {

    /**
     * @return
     */
    Create[] value();

    public static class Processor extends AbsScriptEngine<Creates> {

        @Override
        public void doProcess(TestContext context, Creates t) throws Throwable {
            // create VariableContext
            for ( Create create : t.value() ) {
                this.executeScript(context, create);
            }
            context.getBase().evaluate();
            // destroy VariableContext
        }
    }
}
