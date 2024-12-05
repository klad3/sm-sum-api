package me.klad3.sumapispring.exception;

import me.klad3.sumapispring.dto.ApiResponse;
import me.klad3.sumapispring.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        ApiResponse<Map<String, String>> response = ApiResponse.error("Validation failed", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleAuthenticationException(AuthenticationException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), "Authentication failed");
        ApiResponse<ErrorResponse> response = ApiResponse.error("Authentication Error", error);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), "Resource not found");
        ApiResponse<ErrorResponse> response = ApiResponse.error("Not Found", error);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleBadRequestException(BadRequestException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), "Bad Request");
        ApiResponse<ErrorResponse> response = ApiResponse.error("Bad Request", error);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApiKeyUnauthorizedException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleUnauthorized(ApiKeyUnauthorizedException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), "Unauthorized");
        ApiResponse<ErrorResponse> response = ApiResponse.error("Unauthorized", error);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleMissingParameter(MissingServletRequestParameterException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), "Missing Request Parameter");
        ApiResponse<ErrorResponse> response = ApiResponse.error("Bad Request", error);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), "Malformed JSON Request");
        ApiResponse<ErrorResponse> response = ApiResponse.error("Bad Request", error);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleResourceAlreadyExists(ResourceAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), "Conflict");
        ApiResponse<ErrorResponse> response = ApiResponse.error("Conflict", error);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleExternalApiException(ExternalApiException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), "External API Error");
        ApiResponse<ErrorResponse> response = ApiResponse.error("External API Error", error);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleAllExceptions(Exception ex) {
        ErrorResponse error = new ErrorResponse("An unexpected error occurred", ex.getMessage());
        ApiResponse<ErrorResponse> response = ApiResponse.error("Internal Server Error", error);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
