package com.khalamai.tictactoe.gameengine.service.impl;

import com.khalamai.tictactoe.gameengine.domain.entity.Cell;
import com.khalamai.tictactoe.common.domain.Board;
import com.khalamai.tictactoe.common.domain.enums.GameStatus;
import com.khalamai.tictactoe.common.domain.enums.Symbol;
import com.khalamai.tictactoe.gameengine.service.BoardService;
import com.khalamai.tictactoe.gameengine.service.WinDetectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class WinDetectorServiceImpl implements WinDetectorService {

    private static final int[][][] WINNING_LINES = {
            {{0, 0}, {0, 1}, {0, 2}}, // rows
            {{1, 0}, {1, 1}, {1, 2}},
            {{2, 0}, {2, 1}, {2, 2}},
            {{0, 0}, {1, 0}, {2, 0}}, // columns
            {{0, 1}, {1, 1}, {2, 1}},
            {{0, 2}, {1, 2}, {2, 2}},
            {{0, 0}, {1, 1}, {2, 2}}, // diagonals
            {{0, 2}, {1, 1}, {2, 0}},
    };

    private final BoardService boardService;

    @Override
    public GameStatus evaluate(Set<Cell> cells, Symbol justMoved) {
        if (isWinningMove(cells, justMoved)) {
            return justMoved == Symbol.X ? GameStatus.X_WON : GameStatus.O_WON;
        }
        return cells.size() == Board.CELL_COUNT ? GameStatus.DRAW : GameStatus.IN_PROGRESS;
    }

    private boolean isWinningMove(Set<Cell> cells, Symbol symbol) {
        return Arrays.stream(WINNING_LINES).anyMatch(line -> lineIsAllSymbol(cells, line, symbol));
    }

    private boolean lineIsAllSymbol(Set<Cell> cells, int[][] line, Symbol symbol) {
        return Arrays.stream(line).allMatch(cell -> boardService.at(cells, cell[0], cell[1]) == symbol);
    }
}
