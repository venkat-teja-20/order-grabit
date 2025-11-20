package com.grabit.enums;

import lombok.Getter;

public enum CommonErrors {
    unknown_error("Something Went Wrong"),
    ORDER_NOT_FOUND("No order found with provided Order Id"),
    ORDER_ITEMS_MISSING("Order Items are Required to Place an Order"),
    REQUEST_BODY_MISSING("Request Body is required for this operation"),
    ACCESS_DENIED("You do not have the permission to access this resource"),
    AUTHENTICATION_FAILED("Error decoding signature"),
    AUTHENTICATION_EXPIRED("Signature has expired"),
    AUTHENTICATION_REQUIRED("User is not authenticated"),
    Forbidden("You do not have the permission to access this resource"),
    INVALID_ORDER_STATUS("Order Status Provided is Not Valid");

    @Getter
    private String message;

    CommonErrors(String details) {
        this.message = details;
    }
}
