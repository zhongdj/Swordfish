/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.madz.binding.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Target: In order to convert a business object, which may contain some complex
 * type fields, to a transfer object. <br/>
 * Such as: Warehouse --> WarehouseCTO<br/>
 * <br/>
 * [Class Warehouse]<br/>
 * private Long id;<br/>
 * private String name;<br/>
 * private WarehouseType type; <--- complex type field ...<br/>
 * <br/>
 * [Class WarehouseType]<br/>
 * private Long id; <--- simple type field<br/>
 * private String name; <--- simple type field ...<br/>
 * <br/>
 * [Class WarehouseCTO]:<br/>
 * protected String name;<br/>
 * 
 * *@Binding(name = "type.id") <--- Warehouse.type@WarehouseType.id@Long <br/>
 * *private Long typeId;<br/>
 * *@Binding(name = "type.name") <--- Warehouse.type@WarehouseType.name@String<br/>
 * *private String typeName; ...<br/>
 * 
 * @author Barry
 */
// TODO [Tracy] [Done] [Code Review Task] [Using Ctrl + Shift + G to search
// references in workspace, and design test cases.]
// TODO [Tracy] [Done] [Add Method Comments] [Alt + Shift + J]
// TODO [Tracy] [Done] [Study][@Target & @Retention]
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Binding {

    /**
     * The name is composed by several parts and joined by '.', such as xx.yy xx
     * represents the complex field; yy represents the simple type field of the
     * complex field
     * 
     * @return name of a complex field's simple type field.
     */
    String name() default "";

    AccessTypeEnum accessType() default AccessTypeEnum.Field;

    BindingTypeEnum bindingType() default BindingTypeEnum.Field;

    Class embeddedType() default Object.class;
}
