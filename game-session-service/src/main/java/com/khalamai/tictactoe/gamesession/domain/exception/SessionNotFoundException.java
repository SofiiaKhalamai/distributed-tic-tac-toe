package com.khalamai.tictactoe.gamesession.domain.exception;

public class SessionNotFoundException extends RuntimeException {

    public SessionNotFoundException(String sessionId) {
        super("Session not found: %s".formatted(sessionId));
    }
}
