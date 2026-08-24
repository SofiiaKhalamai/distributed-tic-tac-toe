package com.khalamai.tictactoe.gameengine.domain.exception;

public class GameAlreadyFinishedException extends RuntimeException {

    public GameAlreadyFinishedException(String gameId) {
        super("Game already finished: %s".formatted(gameId));
    }
}
