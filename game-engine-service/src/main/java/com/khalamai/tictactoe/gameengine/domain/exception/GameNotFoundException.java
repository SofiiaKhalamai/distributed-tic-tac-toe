package com.khalamai.tictactoe.gameengine.domain.exception;

public class GameNotFoundException extends RuntimeException {

    public GameNotFoundException(String gameId) {
        super("Game not found: %s".formatted(gameId));
    }
}
