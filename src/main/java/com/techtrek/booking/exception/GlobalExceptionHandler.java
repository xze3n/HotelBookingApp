package com.techtrek.booking.exception;

import com.techtrek.booking.exception.base.BaseException;
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

    // All custom exceptions : UserNotFoundException, RoomNotAvailableException ...
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(
            BaseException ex,
            WebRequest request
    ) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                ex.getStatus().value(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, ex.getStatus());
    }

    // Validation: @NotBlank,@NotEmpty ...
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation (
            MethodArgumentNotValidException ex,
            WebRequest request
    ) {
        String message = (ex.getBindingResult().getFieldError() != null)
                ? ex.getBindingResult().getFieldError().getDefaultMessage()
                : "Invalid Data";

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                message,
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Fallback : any other unexpected exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ignored,
            WebRequest request
    ) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
