package com.grabit.config;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.grabit.Utilities.Utility;
import com.grabit.enums.CommonErrors;
import com.grabit.exception.APIError;
import com.grabit.exception.CustomException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Log4j2
public class APIExceptionHandler {

    @ExceptionHandler(value = CustomException.class)
    public APIError handleCustomException(CustomException ex, HttpServletResponse response) {
        response.setStatus(ex.getErrorObject().getHttpCode());
        return ex.getErrorObject().getErrorMsg();
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public APIError handleIllegalArgumentException(IllegalArgumentException ex, HttpServletResponse response) {
        response.setStatus(406);
        String errorMsg = ex.getMessage();
        if (errorMsg.contains("Page"))
            return new APIError("INVALID_PARAMETER", errorMsg);
        return new APIError("INVALID_ARGUMENT", errorMsg);
    }

    @ExceptionHandler(value = PropertyReferenceException.class)
    public APIError handlePropertyReferenceException(PropertyReferenceException ex, HttpServletResponse response) {
        response.setStatus(406);
        return new APIError("INVALID_FIELD_NAME", ex.getMessage());
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public APIError handleDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletResponse response) {
        response.setStatus(400);
        return new APIError("BAD_REQUEST", ex.getMessage());
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public APIError handleEntityNotFoundException(EntityNotFoundException ex, HttpServletResponse response) {
        response.setStatus(404);
        String msg = Utility.isNullOrEmpty(ex.getMessage()) ? CommonErrors.ORDER_NOT_FOUND.getMessage() : ex.getMessage();
        return new APIError(CommonErrors.ORDER_NOT_FOUND.toString(), msg);
    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    public APIError handleNoResourceFoundException(NoResourceFoundException ex, HttpServletResponse response) {
        response.setStatus(ex.getBody().getStatus());
        return new APIError("INVALID_REQUEST_PATH", "The requested resource " + ex.getResourcePath() + " does not exist");
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public APIError handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, HttpServletResponse response) {
        response.setStatus(ex.getBody().getStatus());
        return new APIError("METHOD_NOT_ALLOWED", ex.getBody().getDetail());
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public APIError handleConstraintViolationException(ConstraintViolationException ex, HttpServletResponse response) {
        response.setStatus(406);
        if (ex.getConstraintViolations().iterator().next().getMessageTemplate().contains("NotBlank") || ex.getConstraintViolations().iterator().next().getMessageTemplate().contains("NotNull")) {
            response.setStatus(400);
            return new APIError("REQUIRED_FIELD_MISSING", ex.getConstraintViolations().iterator().next().getPropertyPath() + " must not be null or empty");
        }
        Integer max = (Integer) ex.getConstraintViolations().iterator().next().getConstraintDescriptor().getAttributes().get("max");
        Integer min = (Integer) ex.getConstraintViolations().iterator().next().getConstraintDescriptor().getAttributes().get("min");
        String message = "";
        String field = String.valueOf(ex.getConstraintViolations().iterator().next().getPropertyPath()).toUpperCase();
        if (min!=null && min == 0)
            message = field + " can contain up to " +
                    max + " characters";
        else if (min != null && min.equals(max))
            message = field + " must be exactly " +
                    max + " characters";
        else
            message = field + " should have a minimum of " +
                    min + " and a maximum of " + max + " characters";
        return new APIError("CONSTRAINT_LIMIT", message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public APIError handleHttpMessageNotReadable(HttpMessageNotReadableException ex,HttpServletResponse response) {
        Throwable cause = ex.getMostSpecificCause();
        response.setStatus(406);
        List<String> typeList = new ArrayList<>(List.of("number", "double", "integer","long"));
        if (cause instanceof InvalidFormatException invalidFormatEx)
            if(typeList.contains(invalidFormatEx.getTargetType().getSimpleName().toLowerCase())){
                return new APIError("INVALID_INPUT", "Expected value of type Number in " + invalidFormatEx.getPath().getFirst().getFieldName() + " but received : " + invalidFormatEx.getValue());
            }
            else if("boolean".equalsIgnoreCase(invalidFormatEx.getTargetType().getSimpleName())){
                return new APIError("INVALID_INPUT", "Expected true or false in " + invalidFormatEx.getPath().getFirst().getFieldName() + " but received : " + invalidFormatEx.getValue());
            }
            else
                return new APIError("INVALID_INPUT", "Expected value of type Text in " + invalidFormatEx.getPath().getFirst().getFieldName() + " but received : " + invalidFormatEx.getValue());
        else if(cause instanceof MismatchedInputException){
            response.setStatus(400);
            return new APIError("REQUEST_BODY_MISSING", "Request body is required");
        }
        else if(cause instanceof CustomException e){
            return e.getErrorObject().getErrorMsg();
        }
        else
            return new APIError("INVALID_INPUT", cause.getMessage());
    }

    @ExceptionHandler(value = HttpClientErrorException.class)
    public APIError handleHttpClientErrorException(HttpClientErrorException ex, HttpServletResponse response) {
        log.info("Microservice Response Status : "+ex.getStatusCode().value());
        try {
            response.setStatus(ex.getStatusCode().value());
            return ex.getResponseBodyAs(APIError.class);
        }
        catch (Exception e){
            log.info("Microservice Response : "+ex.getResponseBodyAsString());
            response.setStatus(500);
            return new APIError("REQUEST_FAILED","Request to the microservice failed");
        }
    }

    @ExceptionHandler(value = Exception.class)
    public APIError handleException(Exception ex, HttpServletResponse response) {
        log.info("Unknown Issue Occurred : " + ex.getMessage());
        if(ex instanceof AccessDeniedException)
            throw (AccessDeniedException) ex;
        response.setStatus(500);
        return new APIError(CommonErrors.unknown_error.toString(), CommonErrors.unknown_error.getMessage());
    }

}
