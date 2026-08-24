package com.khalamai.tictactoe.gamesession.client;

import com.khalamai.tictactoe.common.domain.enums.Symbol;

public record EngineMoveRequest(Symbol symbol, int row, int col) {
}
