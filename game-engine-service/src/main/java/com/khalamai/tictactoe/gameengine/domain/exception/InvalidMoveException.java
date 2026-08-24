package com.khalamai.tictactoe.gameengine.domain.exception;

public class InvalidMoveException extends RuntimeException {

    public InvalidMoveException(String message) {
        super(message);
    }
}
