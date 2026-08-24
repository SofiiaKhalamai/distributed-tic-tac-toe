package com.khalamai.tictactoe.gamesession.domain.exception;

public class GameEngineCommunicationException extends RuntimeException {

    public GameEngineCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
