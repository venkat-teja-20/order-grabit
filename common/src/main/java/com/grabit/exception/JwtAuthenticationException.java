package com.grabit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class JwtAuthenticationException extends RuntimeException{
    @Getter
    private APIError authenticationError;
}
