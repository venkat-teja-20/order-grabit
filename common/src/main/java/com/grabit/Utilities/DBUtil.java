package com.grabit.Utilities;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class DBUtil {

    public static void copyNonNullVariables(Object source,Object target){
        if(Utility.isNullOrEmpty(source) || Utility.isNullOrEmpty(target)){
            return;
        }

        BeanWrapper srcWrapperImpl=new BeanWrapperImpl(source);
        BeanWrapper tgtWrapperImpl=new BeanWrapperImpl(target);

        for(var pd:srcWrapperImpl.getPropertyDescriptors()){
            String propertyName=pd.getName();

            if("class".equals(propertyName))
                continue;

            Object srcValue=srcWrapperImpl.getPropertyValue(propertyName);

            if(srcValue==null)
                continue;

            Object tgtValue=tgtWrapperImpl.getPropertyValue(propertyName);

            if(!isPrimitiveOrWrapper(srcValue.getClass()) || !(srcValue instanceof String)){
                if(tgtValue==null)
                    tgtWrapperImpl.setPropertyValue(propertyName,srcValue);
                else
                    copyNonNullVariables(srcValue,tgtValue);
            }
            else
                tgtWrapperImpl.setPropertyValue(propertyName,srcValue);
        }
    }

    private static boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive() ||
                type.equals(Boolean.class) || type.equals(Integer.class) ||
                type.equals(Character.class) || type.equals(Byte.class) ||
                type.equals(Short.class) || type.equals(Double.class) ||
                type.equals(Long.class) || type.equals(Float.class);
    }
}
