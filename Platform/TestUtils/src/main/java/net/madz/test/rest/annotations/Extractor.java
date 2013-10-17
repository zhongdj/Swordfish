package net.madz.test.rest.annotations;

/**
 * The @Extrator describes mapping metadata to map from RESTful POST
 * Response expression to a context variable.<br/>
 * <br/>
 * For example, <br/>
 * <b>@Extractor(key="pouringPartSpecIds[0].id",
 * var="specId") </b><br/>
 * is to map from the 1st id from response is going to
 * be mapped with a context variable ${specId} in context.<br/>
 * <br>
 * Response is as following:<br/>
 * {<br/>
 * &nbsp;&nbsp;"pouringPartSpecIds": [<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;{<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id": 61<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;},<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;{<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id": 63<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;}<br/>
 * &nbsp;&nbsp;]<br/>
 * }<br/>
 * 
 * @author Tracy Lu
 * 
 */
public @interface Extractor {

    /**
     * @return RESTful response expression, such as
     *         <i><b>pouringPartSpec[0].id</b></i>
     */
    String key();

    /**
     * @return Context variable name, such as a context variable
     *         <i><b>${specId}</b></i> can
     *         be represented by <i><b> var="specId" </b></i>
     */
    String var();
}
