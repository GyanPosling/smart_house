package com.avelina_anton.bzhch.smart_house.demo.utllis;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class SmartHomeExceptionHandler {

    @ExceptionHandler(SmartHomeException.class)
    public ResponseEntity<SmartHomeErrorResponse> handleException(SmartHomeException e) {
        SmartHomeErrorResponse response = new SmartHomeErrorResponse(
                e.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DeviceNotFoundException.class)
    public ResponseEntity<SmartHomeErrorResponse> handleException(DeviceNotFoundException e) {
        SmartHomeErrorResponse response = new SmartHomeErrorResponse(
                e.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SensorNotFoundException.class)
    public ResponseEntity<SmartHomeErrorResponse> handleException(SensorNotFoundException e) {
        SmartHomeErrorResponse response = new SmartHomeErrorResponse(
                e.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SmartHomeNotFoundException.class)
    public ResponseEntity<SmartHomeErrorResponse> handleException(SmartHomeNotFoundException e) {
        SmartHomeErrorResponse response = new SmartHomeErrorResponse(
                e.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<SmartHomeErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String errorMessage = ErrorsUtil.getErrorMessage(bindingResult);

        SmartHomeErrorResponse response = new SmartHomeErrorResponse(
                errorMessage,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SmartHomeErrorResponse> handleException(Exception e) {
        SmartHomeErrorResponse response = new SmartHomeErrorResponse(
                "Внутренняя ошибка сервера: " + e.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}