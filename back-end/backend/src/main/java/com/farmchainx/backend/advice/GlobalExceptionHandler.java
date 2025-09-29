package com.farmchainx.backend.advice;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(AccessDeniedException ex) {
        return "redirect:/access-denied";
    }

    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception ex) {
        return "redirect:/error";
    }
}