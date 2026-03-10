package com.techtrek.booking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.techtrek.booking.dto.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Resource missing in database
    // (e.g., requested Room ID does not exist)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> resourceNotFound (
            ResourceNotFoundException ex,
            WebRequest request
    ){
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(
                error,
                HttpStatus.NOT_FOUND
                );
    }

    // Invalid Argument like Type == null
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> invalidArgument(
            MethodArgumentNotValidException ex,
            WebRequest request
    ) {
        String errorResponse = (ex.getBindingResult().getFieldError() != null) ?
                ex.getBindingResult().getFieldError().getDefaultMessage() :
                "Invalid data";

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                errorResponse,
                request.getDescription(false)
        );

        return new ResponseEntity<>(error,
                HttpStatus.BAD_REQUEST
        );
    }
}
