package com.grabit.exception;

public class CustomException extends BasicException {
    public CustomException(String message) {
        super(message);
    }

    public CustomException(ErrorObject errorObject){
        super(errorObject);
    }
}
