package net.madz.test.rest.annotations;

/**
 * The @MergeField describes merging metadata mapping from context
 * variable to RESTFul POST request entity template merge field. <br/>
 * <br/>
 * For example, <i><b>@MergeField(key="pouringPartSpecId",var="specId" )
 * </b></i><br/>
 * describes merging metadata from context
 * variable specId to the merge field <i><b>${pouringPartSpecId}</b></i> inside
 * request entity template.<br/>
 * RESTFul POST request entity template:<i>
 * {"specId":<i><b>${pouringPartSpecId}</b></i>,"totalVolume":30}
 * 
 * @author Tracy Lu
 * 
 */
public @interface MergeField {

    /**
     * @return merge field name, such as <i><b>${pouringPartSpecId}</b></i> in
     *         template can be represented as
     *         <i><b>key="pouringPartSpecId"</b></i>.
     */
    String key();

    /**
     * @return context variable name, such as a context variable
     *         <i><b>${specId}</b></i> can be represent as
     *         <i><b>var="specId"</b></i>.
     */
    String var();
}
