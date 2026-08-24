package com.khalamai.tictactoe.common.domain.enums;

public enum GameStatus {
    IN_PROGRESS,
    X_WON,
    O_WON,
    DRAW;

    public boolean isFinished() {
        return this != IN_PROGRESS;
    }
}
