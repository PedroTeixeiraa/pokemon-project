package com.example.pokemon.exception;

import com.example.pokemon.exception.auth.EmailAlreadyRegisteredException;
import com.example.pokemon.exception.auth.InvalidCredentialsException;
import com.example.pokemon.exception.pokemon.PokemonNotFoundException;
import com.example.pokemon.exception.pokemon.PokemonNotSyncedException;
import com.example.pokemon.exception.integration.PokeApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> validation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        Map<String, String> fields = new LinkedHashMap<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            fields.put(error.getField(), error.getDefaultMessage());
        }
        return response(HttpStatus.BAD_REQUEST, "Payload validation failed", request, fields);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiError> malformedPayload(HttpMessageNotReadableException exception, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, "Malformed JSON payload", request, Map.of());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ApiError> badRequest(IllegalArgumentException exception, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, exception.getMessage(), request, Map.of());
    }

    @ExceptionHandler(PokemonNotFoundException.class)
    ResponseEntity<ApiError> notFound(PokemonNotFoundException exception, HttpServletRequest request) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage(), request, Map.of());
    }

    @ExceptionHandler({EmailAlreadyRegisteredException.class, PokemonNotSyncedException.class})
    ResponseEntity<ApiError> conflict(RuntimeException exception, HttpServletRequest request) {
        return response(HttpStatus.CONFLICT, exception.getMessage(), request, Map.of());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    ResponseEntity<ApiError> unauthorized(InvalidCredentialsException exception, HttpServletRequest request) {
        return response(HttpStatus.UNAUTHORIZED, exception.getMessage(), request, Map.of());
    }

    @ExceptionHandler(PokeApiException.class)
    ResponseEntity<ApiError> upstream(PokeApiException exception, HttpServletRequest request) {
        return response(HttpStatus.BAD_GATEWAY, exception.getMessage(), request, Map.of());
    }

    private ResponseEntity<ApiError> response(HttpStatus status, String message, HttpServletRequest request,
                                              Map<String, String> fields) {
        return ResponseEntity.status(status).body(new ApiError(status.value(), message, Instant.now(), request.getRequestURI(), fields));
    }
}
