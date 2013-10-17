package net.madz.test.rest.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The @Creates is a test script construct that defines a set of @Create
 * 
 * @author Tracy Lu
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Creates {

    /**
     * @return
     */
    Create[] value();
}
