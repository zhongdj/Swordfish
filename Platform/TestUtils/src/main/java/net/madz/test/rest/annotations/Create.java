package net.madz.test.rest.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
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
}
