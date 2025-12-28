package com.grabit.Utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.grabit.exception.APIError;
import com.grabit.exception.ErrorObject;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

@Log4j2
public class Utility {
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static final ObjectMapper SNAKE_CASE_OBJECT_MAPPER = new ObjectMapper();

    public static String toJson(Object o) {
        try {
            OBJECT_MAPPER.registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return OBJECT_MAPPER.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.error(e);
            return null;
        }
    }

    public static ErrorObject buildErrorObject(String code, String message, int httpStatusCode, String service) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setErrorMsg(new APIError(code,message));
        errorObject.setHttpCode(httpStatusCode);
        errorObject.setService(service);
        return errorObject;
    }

    public static String toJsonSnakeCase(Object o) {
        try {
            return SNAKE_CASE_OBJECT_MAPPER.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.error(e);
            return null;
        }
    }

    public static Boolean isNullOrEmpty(Object o) {
        return o == null || o.toString().trim().isEmpty();
    }

    public static Boolean isNumeric(String str) {
        return str.matches("\\d+");
    }

    public static Map<String,String> buildHeader(Map<String,String> headers){
        if(isNullOrEmpty(headers))
            return new HashMap<>();
        Map<String,String> newHeader=new HashMap<>();
        String authorization=isNullOrEmpty(headers.get("Authorization"))?headers.get("authorization"):headers.get("Authorization");
        newHeader.put("Authorization",authorization);
        if(!isNullOrEmpty(headers.get("x-forwarded-for")))
            newHeader.put("x-forwarded-for",headers.get("x-forwarded-for"));
        return newHeader;
    }

    public static HttpHeaders buildHttpHeader(Map<String,String> headers){
        HttpHeaders httpHeaders=new HttpHeaders();
        httpHeaders.setContentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE));
        if(isNullOrEmpty(headers))
            return httpHeaders;
        String authorization=isNullOrEmpty(headers.get("Authorization"))?headers.get("authorization"):headers.get("Authorization");
        httpHeaders.set("Authorization",authorization);
        if(!isNullOrEmpty(headers.get("x-forwarded-for")))
            httpHeaders.set("x-forwarded-for",headers.get("x-forwarded-for"));
        return httpHeaders;
    }
}
