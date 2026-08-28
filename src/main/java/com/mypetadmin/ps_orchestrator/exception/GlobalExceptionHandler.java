package com.mypetadmin.ps_orchestrator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OnboardingDependencyException.class)
    ResponseEntity<ErrorResponse> dependency(OnboardingDependencyException ex) {
        HttpStatus status;
        String code;
        if (ex.getUpstreamStatus() == 409) {
            status = HttpStatus.CONFLICT;
            code = "ONBOARDING_CONFLICT";
        } else if (ex.getUpstreamStatus() == 400 || ex.getUpstreamStatus() == 422) {
            status = HttpStatus.BAD_REQUEST;
            code = "ONBOARDING_REJECTED";
        } else {
            status = HttpStatus.BAD_GATEWAY;
            code = "ONBOARDING_DEPENDENCY_ERROR";
        }
        return response(status, code, ex.getMessage(), ex.getStep());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, MissingRequestHeaderException.class})
    ResponseEntity<ErrorResponse> validation(Exception ex) {
        return response(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "Dados do onboarding são inválidos.",
                null);
    }

    private ResponseEntity<ErrorResponse> response(HttpStatus status, String code, String message, String step) {
        return ResponseEntity.status(status).body(
                new ErrorResponse(Instant.now(), status.value(), code, message, step));
    }
}
