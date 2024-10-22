package com.khanhdang.library_manage.exception;

import com.khanhdang.library_manage.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

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

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ApiResponse<?>> handleUserException(UserException ex) {
        ApiResponse<?> response = ApiResponse.builder()
                .status(ApiStatus.ERROR)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    @ExceptionHandler(AuthenticateException.class)
    public ResponseEntity<ApiResponse<?>> handleUserException(AuthenticateException ex) {
        ApiResponse<?> response = ApiResponse.builder()
                .status(ApiStatus.ERROR)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    @ExceptionHandler(RoleException.class)
    public ResponseEntity<ApiResponse<?>> handleRoleException(RoleException ex) {
        ApiResponse<?> response = ApiResponse.builder()
                .status(ApiStatus.ERROR)
                .message("An unexpected error occurred: " + ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGlobalException(Exception ex) {
        ApiResponse<?> response = ApiResponse.builder()
                .status(ApiStatus.ERROR)
                .message("An unexpected error occurred: " + ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Lấy tất cả các thông báo lỗi từ FieldError
        List<String> errorMessages = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getDefaultMessage()) // Lấy thông báo lỗi trực tiếp từ FieldError
                .collect(Collectors.toList());
        // Trả về thông báo lỗi đầu tiên
        ApiResponse<?> response = ApiResponse.builder()
                .status(ApiStatus.ERROR)
                .message(errorMessages.get(0)) // Chỉ lấy lỗi đầu tiên
                .build();
        return ResponseEntity.badRequest().body(response);
    }
}
