package com.technicalchallenge.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class TradeGlobalExceptionHandler {

        private static final Logger loggerErr = LoggerFactory.getLogger(TradeGlobalExceptionHandler.class);

    private ResponseEntity<Object> response(HttpStatus status, String message, WebRequest request){
        Map<String, Object> bodyOfMessage = new LinkedHashMap<>();
        bodyOfMessage.put("timestamp", LocalDate.now());
        bodyOfMessage.put("status", status.value());
        bodyOfMessage.put("error", status.getReasonPhrase());
        bodyOfMessage.put("message", message);
        return new ResponseEntity<>(bodyOfMessage, status);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException (MethodArgumentNotValidException except, WebRequest request) {
        List<String> validationError = new ArrayList<>();
        for (FieldError error : except.getBindingResult().getFieldErrors()) {
            validationError.add(error.getDefaultMessage());
        }

        String errorMessageJoin = String.join(";", validationError);
        loggerErr.warn("Validation error triggered: {}", errorMessageJoin);
        return response(HttpStatus.BAD_REQUEST, errorMessageJoin, request);
    }
}
