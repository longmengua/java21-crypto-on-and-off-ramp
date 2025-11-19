package com.example.demo.middleware;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> handleNotFound(NoHandlerFoundException ex) {
        // 返回 504 狀態碼
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(HttpStatus.BAD_GATEWAY.getReasonPhrase());
    }
}

