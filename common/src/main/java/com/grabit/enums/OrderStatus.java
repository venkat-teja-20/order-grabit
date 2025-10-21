package com.grabit.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.grabit.Utilities.Utility;
import com.grabit.exception.CustomException;

public enum OrderStatus {
    PLACED,

    PREPARING,

    PICKED_UP,

    ARRIVED_AT_LOCATION,

    DELIVERED,

    CANCELLED,

    REFUND_REQUESTED,

    REFUND_COMPLETED,

    REFUND_INITIATED,

    ON_HOLD;

    @JsonCreator
    public static OrderStatus fromValue(String value){
        for(OrderStatus status:values()){
            if(value.equalsIgnoreCase(status.name()))
                return status;
        }
        throw new CustomException(Utility.buildErrorObject("INVALID_INPUT","Invalid OrderStatus : "+value,406,"PaymentMethodEnum"));
    }

    @JsonValue
    public String toValue(){
        return this.name();
    }
}
