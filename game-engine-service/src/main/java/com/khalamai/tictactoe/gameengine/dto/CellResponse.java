package com.khalamai.tictactoe.gameengine.dto;

import com.khalamai.tictactoe.common.domain.enums.Symbol;

public record CellResponse(int row, int col, Symbol symbol) {
}
