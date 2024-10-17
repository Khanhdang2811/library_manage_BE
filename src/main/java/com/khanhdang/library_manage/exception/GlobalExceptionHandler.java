package com.khanhdang.library_manage.exception;

import com.khanhdang.library_manage.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BookException.class)
    public ResponseEntity<ApiResponse<?>> handleBookNotFoundException(BookException ex) {
        ApiResponse<?> response = ApiResponse.builder()
                .status(ApiStatus.ERROR)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGlobalException(Exception ex) {
        ApiResponse<?> response = ApiResponse.builder()
                .status(ApiStatus.ERROR)
                .message("An unexpected error occurred: " + ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
