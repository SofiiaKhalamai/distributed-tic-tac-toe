package com.khalamai.tictactoe.gamesession.dto;

import com.khalamai.tictactoe.common.domain.enums.GameStatus;
import com.khalamai.tictactoe.gamesession.domain.enums.SessionStatus;

import java.util.List;

public record SessionResponse(
        String sessionId,
        SessionStatus status,
        GameStatus gameStatus,
        List<CellResponse> board,
        List<MoveRecordResponse> moveHistory
) {
}
