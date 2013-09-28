package net.madz.scheduling.biz;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import net.madz.core.biz.IBizObject;

public class Test {

    public static void main(String[] args) {
        Type[] genericInterfaces = IServiceOrder.class.getGenericInterfaces();
        for ( Type type : genericInterfaces ) {
            if ( type instanceof ParameterizedType ) {
                ParameterizedType pt = (ParameterizedType) type;
                if ( pt.getRawType().equals(IBizObject.class) ) {
                    if ( pt.getActualTypeArguments().length == 1 ) {
                        System.out.println(pt.getActualTypeArguments()[0]);
                    }
                }
            }
        }
    }
}
