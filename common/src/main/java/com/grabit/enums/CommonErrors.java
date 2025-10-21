package com.grabit.enums;

import lombok.Getter;

public enum CommonErrors {
    unknown_error("Something Went Wrong"),
    ORDER_NOT_FOUND("No order found with provided Order Id"),
    ORDER_ITEMS_MISSING("Order Items are Required to Place an Order"),
    REQUEST_BODY_MISSING("Request Body is required for this operation"),
    INVALID_ORDER_STATUS("Order Status Provided is Not Valid");

    @Getter
    private String message;

    CommonErrors(String details) {
        this.message = details;
    }
}
