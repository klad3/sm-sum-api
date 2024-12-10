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

    private static final String BAD_REQUEST = "Bad Request";
    private static final String UNAUTHORIZED = "Unauthorized";
    private static final String NOT_FOUND = "Not Found";
    private static final String CONFLICT = "Conflict";
    private static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    private static final String VALIDATION_FAILED = "Validation failed";
    private static final String AUTHENTICATION_FAILED = "Authentication failed";
    private static final String RESOURCE_NOT_FOUND = "Resource not found";
    private static final String MALFORMED_JSON_REQUEST = "Malformed JSON Request";
    private static final String EXTERNAL_API_ERROR = "External API Error";
    private static final String INTERNAL_ERROR_MESSAGE = "An unexpected error occurred";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        ApiResponse<Map<String, String>> response = ApiResponse.error(VALIDATION_FAILED, errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleAuthenticationException(AuthenticationException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), AUTHENTICATION_FAILED);
        ApiResponse<ErrorResponse> response = ApiResponse.error(AUTHENTICATION_FAILED, error);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), RESOURCE_NOT_FOUND);
        ApiResponse<ErrorResponse> response = ApiResponse.error(NOT_FOUND, error);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleBadRequestException(BadRequestException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), BAD_REQUEST);
        ApiResponse<ErrorResponse> response = ApiResponse.error(BAD_REQUEST, error);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApiKeyUnauthorizedException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleUnauthorized(ApiKeyUnauthorizedException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), UNAUTHORIZED);
        ApiResponse<ErrorResponse> response = ApiResponse.error(UNAUTHORIZED, error);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleMissingParameter(MissingServletRequestParameterException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), BAD_REQUEST);
        ApiResponse<ErrorResponse> response = ApiResponse.error(BAD_REQUEST, error);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), MALFORMED_JSON_REQUEST);
        ApiResponse<ErrorResponse> response = ApiResponse.error(BAD_REQUEST, error);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleResourceAlreadyExists(ResourceAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), CONFLICT);
        ApiResponse<ErrorResponse> response = ApiResponse.error(CONFLICT, error);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleExternalApiException(ExternalApiException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), EXTERNAL_API_ERROR);
        ApiResponse<ErrorResponse> response = ApiResponse.error(EXTERNAL_API_ERROR, error);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleAllExceptions(Exception ex) {
        ErrorResponse error = new ErrorResponse(INTERNAL_ERROR_MESSAGE, ex.getMessage());
        ApiResponse<ErrorResponse> response = ApiResponse.error(INTERNAL_SERVER_ERROR, error);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
