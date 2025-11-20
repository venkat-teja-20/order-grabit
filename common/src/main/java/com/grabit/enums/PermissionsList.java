package com.grabit.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.grabit.Utilities.Utility;
import com.grabit.exception.CustomException;

public enum PermissionsList {
    END_USER,

    VIEW_MEMBER,

    EDIT_MEMBER,

    DELETE_MEMBER,

    VIEW_PARTNER,

    EDIT_PARTNER,

    DELETE_PARTNER,

    VIEW_ORDER,

    PLACE_ORDER,

    EDIT_ORDER,

    DELETE_ORDER,

    VIEW_RESTAURANT,

    EDIT_RESTAURANT,

    DELETE_RESTAURANT,

    VIEW_BRANCH,

    EDIT_BRANCH,

    DELETE_BRANCH,

    VIEW_ITEMS,

    EDIT_ITEMS,

    DELETE_ITEMS,

    VIEW_ROLE,

    VIEW_PERMISSION;

    @JsonCreator
    public static PermissionsList fromValue(String value){
        for(PermissionsList permission:values()){
            if(value.equalsIgnoreCase(permission.name()))
                return permission;
        }
        throw new CustomException(Utility.buildErrorObject("INVALID_PERMISSION","No such permission exists : "+value,400,"Role"));
    }

    @JsonValue
    public String toValue(){
        return this.name();
    }
}
