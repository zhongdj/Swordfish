package net.madz.lifecycle.meta.impl.builder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;


public class Test {
    
    List<String> stringList = new ArrayList<String>();
    List<Integer> integerList = new ArrayList<Integer>();
    
    public List<String> getString() {
        return null;
    }

    public String[] getKeys() {
        return null;
    }
    public static void main(String... args) throws Exception {
        Field stringListField = Test.class.getDeclaredField("stringList");
        ParameterizedType stringListType = (ParameterizedType) stringListField.getGenericType();
        Class<?> stringListClass = (Class<?>) stringListType.getActualTypeArguments()[0];
        System.out.println(stringListClass); // class java.lang.String.

        Field integerListField = Test.class.getDeclaredField("integerList");
        ParameterizedType integerListType = (ParameterizedType) integerListField.getGenericType();
        Class<?> integerListClass = (Class<?>) integerListType.getActualTypeArguments()[0];
        System.out.println(integerListClass); // class java.lang.Integer.
        
        Method method = Test.class.getDeclaredMethod("getString");
        Class<?> returnType = method.getReturnType();
        System.out.println("Return Type:" + returnType);
        Method method2 = Test.class.getDeclaredMethod("getKeys");
        returnType = method2.getReturnType();
        if (returnType.isArray()) {
            System.out.println("It is an array");
        }
        System.out.println("Return Type:" + returnType.getComponentType());
        
       
    }
}
