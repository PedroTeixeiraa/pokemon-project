package com.example.pokemon.exception;

import java.time.Instant;
import java.util.Map;

public record ApiError(int status, String message, Instant timestamp, String path, Map<String, String> fields) {
}
