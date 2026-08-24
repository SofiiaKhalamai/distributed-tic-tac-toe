package com.khalamai.tictactoe.gameengine.web.error;

import com.khalamai.tictactoe.common.web.error.AbstractExceptionHandler;
import com.khalamai.tictactoe.common.web.error.ApiError;
import com.khalamai.tictactoe.gameengine.domain.exception.GameAlreadyFinishedException;
import com.khalamai.tictactoe.gameengine.domain.exception.GameNotFoundException;
import com.khalamai.tictactoe.gameengine.domain.exception.InvalidMoveException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler extends AbstractExceptionHandler {

    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(GameNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({InvalidMoveException.class, GameAlreadyFinishedException.class})
    public ResponseEntity<ApiError> handleBadMove(RuntimeException ex) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }
}
