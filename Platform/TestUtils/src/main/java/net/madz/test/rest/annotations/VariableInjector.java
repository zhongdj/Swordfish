package net.madz.test.rest.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.madz.test.rest.annotations.processors.TemplateProcessor;

/**
 * Inject variables into JSON template files.
 * 
 * Such as, while JSON template file contains variable parts, i.e #{userName}
 * and #{password} that can be applied to Basic Authorization RESTful Request,
 * Then specify a TemplateProcessor subclass to VariableInjector.value, which
 * can replace the #{userName} and #{password} with available userName and
 * password information in some context.
 * 
 * 
 * @author Barry
 * 
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface VariableInjector {

    Class<? extends TemplateProcessor> value();
}
