package com.grabit.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.grabit.Utilities.Utility;
import com.grabit.exception.CustomException;

public enum PaymentMethod {
    CASH,

    UPI,

    CARD,

    EWALLET;

    @JsonCreator
    public static PaymentMethod fromValue(String value){
        for(PaymentMethod method:values()){
            if(value.equalsIgnoreCase(method.name()))
                return method;
        }
        throw new CustomException(Utility.buildErrorObject("INVALID_INPUT","Invalid PaymentMethod : "+value,406,"PaymentMethodEnum"));
    }

    @JsonValue
    public String toValue(){
        return this.name();
    }
}
