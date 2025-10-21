package com.grabit.exception;

public class BasicException extends RuntimeException {

    ErrorObject errorObject;

    BasicException(String message) {
        super(message);
    }

    BasicException(ErrorObject errorObject){
        this.errorObject=errorObject;
    }

    public ErrorObject getErrorObject(){
        return errorObject;
    }
}
