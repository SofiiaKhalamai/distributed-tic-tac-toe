package com.khalamai.tictactoe.common.domain.enums;

public enum Symbol {
    X, O;

    public Symbol opponent() {
        return this == X ? O : X;
    }
}
