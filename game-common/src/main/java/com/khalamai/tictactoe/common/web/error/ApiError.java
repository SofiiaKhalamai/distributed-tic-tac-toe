package com.khalamai.tictactoe.common.web.error;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public record ApiError(Instant timestamp, int status, String error, String message) {

    public static ApiError of(HttpStatus status, String message) {
        return new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message);
    }
}
