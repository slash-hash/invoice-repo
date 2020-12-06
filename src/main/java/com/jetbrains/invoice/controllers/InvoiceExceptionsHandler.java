package com.jetbrains.invoice.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.UnexpectedTypeException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;


@ControllerAdvice
public class InvoiceExceptionsHandler {

    Logger log = LoggerFactory.getLogger(InvoiceExceptionsHandler.class);

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpNotReadableException(HttpServletRequest request, Exception exception)  {
        log.error("HttpMessageNotReadableException: {} ", exception);
        return createCustomMessage(exception, HttpStatus.BAD_REQUEST, "cannot read request");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleInvalidInputException(HttpServletRequest request, Exception exception) {
        log.error("ConstraintViolationException: {} ", exception);
        return createCustomMessage(exception, HttpStatus.UNPROCESSABLE_ENTITY, "please check variable types");
    }

    @ExceptionHandler(UnexpectedTypeException.class)
    public ResponseEntity<?> handleWrongType(HttpServletRequest request, Exception exception) {
        log.error("UnexpectedTypeException: {} ", exception);
        return createCustomMessage(exception, HttpStatus.UNPROCESSABLE_ENTITY, "please check variable restrictions");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAnyException(HttpServletRequest request, Exception exception) {
        log.error("Request: {} raised: {} " + request.getRequestURL(), exception);
        return createCustomMessage(exception, HttpStatus.I_AM_A_TEAPOT, "read the full error message :) ");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(HttpServletRequest request, Exception exception) {
        log.error("Request: {} raised: {} " + request.getRequestURL(), exception);
        return createCustomMessage(exception, HttpStatus.BAD_REQUEST, "please check proper specification of request, know that sales system id needs to be unique");
    }

    private ResponseEntity<?> createCustomMessage(Exception exception, HttpStatus httpStatus, String shortDescription) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("status", httpStatus.toString());
        map.put("time", LocalDateTime.now());
        map.put("short_description", shortDescription);
        map.put("error_message", exception.getLocalizedMessage());
        return ResponseEntity.status(httpStatus).body(map);
    }
}
