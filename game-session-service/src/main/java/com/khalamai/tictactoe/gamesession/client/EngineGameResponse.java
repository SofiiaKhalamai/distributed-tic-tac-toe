package com.khalamai.tictactoe.gamesession.client;

import com.khalamai.tictactoe.common.domain.enums.GameStatus;
import com.khalamai.tictactoe.common.domain.enums.Symbol;

import java.util.List;

public record EngineGameResponse(
        String gameId,
        List<EngineCell> board,
        GameStatus status,
        Symbol nextTurn
) {
}
