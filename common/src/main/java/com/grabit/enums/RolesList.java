package com.grabit.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.grabit.Utilities.Utility;
import com.grabit.exception.CustomException;

public enum RolesList {
    USER,

    ADMIN,

    RESTAURANT_OWNER,

    BRANCH_OWNER,

    BRANCH_RECEPTIONIST,

    DELIVERY_PARTNER,

    CUSTOMER_SUPPORT,

    CUSTOMER_MANAGER;

    @JsonCreator
    public static RolesList fromValue(String value){
        for(RolesList role:values()){
            if(value.equalsIgnoreCase(role.name()))
                return role;
        }
        throw new CustomException(Utility.buildErrorObject("INVALID_ROLE","No such Role exists : "+value,400,"Role"));
    }

    @JsonValue
    public String toValue(){
        return this.name();
    }
}
