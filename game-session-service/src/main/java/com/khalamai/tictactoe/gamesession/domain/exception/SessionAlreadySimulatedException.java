package com.khalamai.tictactoe.gamesession.domain.exception;

public class SessionAlreadySimulatedException extends RuntimeException {

    public SessionAlreadySimulatedException(String sessionId) {
        super("Session already completed: %s".formatted(sessionId));
    }
}
