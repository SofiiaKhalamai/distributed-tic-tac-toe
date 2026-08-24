package com.khalamai.tictactoe.gameengine.dto;

import com.khalamai.tictactoe.common.domain.enums.GameStatus;
import com.khalamai.tictactoe.common.domain.enums.Symbol;

import java.util.List;

public record GameResponse(
        String gameId,
        List<CellResponse> board,
        GameStatus status,
        Symbol nextTurn
) {
}
