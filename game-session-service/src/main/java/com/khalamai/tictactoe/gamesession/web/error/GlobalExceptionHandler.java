package com.khalamai.tictactoe.gamesession.web.error;

import com.khalamai.tictactoe.common.web.error.AbstractExceptionHandler;
import com.khalamai.tictactoe.common.web.error.ApiError;
import com.khalamai.tictactoe.gamesession.domain.exception.GameEngineCommunicationException;
import com.khalamai.tictactoe.gamesession.domain.exception.SessionAlreadySimulatedException;
import com.khalamai.tictactoe.gamesession.domain.exception.SessionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler extends AbstractExceptionHandler {

    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(SessionNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(SessionAlreadySimulatedException.class)
    public ResponseEntity<ApiError> handleAlreadySimulated(SessionAlreadySimulatedException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(GameEngineCommunicationException.class)
    public ResponseEntity<ApiError> handleCommunicationFailure(GameEngineCommunicationException ex) {
        return build(HttpStatus.BAD_GATEWAY, ex.getMessage());
    }
}
