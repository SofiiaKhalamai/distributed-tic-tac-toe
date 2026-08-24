package com.khalamai.tictactoe.gamesession.dto;

import com.khalamai.tictactoe.common.domain.enums.Symbol;

public record MoveRecordResponse(int sequence, Symbol symbol, int row, int col) {
}
