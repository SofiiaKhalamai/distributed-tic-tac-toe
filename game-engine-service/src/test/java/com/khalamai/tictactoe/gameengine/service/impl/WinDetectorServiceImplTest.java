package com.khalamai.tictactoe.gameengine.service.impl;

import com.khalamai.tictactoe.gameengine.domain.entity.Cell;
import com.khalamai.tictactoe.common.domain.enums.GameStatus;
import com.khalamai.tictactoe.common.domain.enums.Symbol;
import com.khalamai.tictactoe.gameengine.service.WinDetectorService;
import com.khalamai.tictactoe.gameengine.service.impl.BoardServiceImpl;
import com.khalamai.tictactoe.gameengine.service.impl.WinDetectorServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class WinDetectorServiceImplTest {

    private final WinDetectorService winDetectorService = new WinDetectorServiceImpl(new BoardServiceImpl());

    @Test
    void shouldReportInProgressOnPartialBoard() {
        Set<Cell> cells = new HashSet<>();
        cells.add(new Cell(0, 0, Symbol.X));

        assertThat(winDetectorService.evaluate(cells, Symbol.X)).isEqualTo(GameStatus.IN_PROGRESS);
    }

    @Test
    void shouldDetectRowWin() {
        Set<Cell> cells = new HashSet<>();
        cells.add(new Cell(1, 0, Symbol.X));
        cells.add(new Cell(1, 1, Symbol.X));
        cells.add(new Cell(1, 2, Symbol.X));

        assertThat(winDetectorService.evaluate(cells, Symbol.X)).isEqualTo(GameStatus.X_WON);
    }

    @Test
    void shouldDetectColumnWin() {
        Set<Cell> cells = new HashSet<>();
        cells.add(new Cell(0, 2, Symbol.O));
        cells.add(new Cell(1, 2, Symbol.O));
        cells.add(new Cell(2, 2, Symbol.O));

        assertThat(winDetectorService.evaluate(cells, Symbol.O)).isEqualTo(GameStatus.O_WON);
    }

    @Test
    void shouldDetectDiagonalWin() {
        Set<Cell> cells = new HashSet<>();
        cells.add(new Cell(0, 0, Symbol.X));
        cells.add(new Cell(1, 1, Symbol.X));
        cells.add(new Cell(2, 2, Symbol.X));

        assertThat(winDetectorService.evaluate(cells, Symbol.X)).isEqualTo(GameStatus.X_WON);
    }

    @Test
    void shouldDetectAntiDiagonalWin() {
        Set<Cell> cells = new HashSet<>();
        cells.add(new Cell(0, 2, Symbol.O));
        cells.add(new Cell(1, 1, Symbol.O));
        cells.add(new Cell(2, 0, Symbol.O));

        assertThat(winDetectorService.evaluate(cells, Symbol.O)).isEqualTo(GameStatus.O_WON);
    }

    @Test
    void shouldDetectDrawOnFullBoardWithNoWin() {
        Set<Cell> cells = new HashSet<>();
        // X O X
        // X O O
        // O X X
        cells.add(new Cell(0, 0, Symbol.X));
        cells.add(new Cell(0, 1, Symbol.O));
        cells.add(new Cell(0, 2, Symbol.X));
        cells.add(new Cell(1, 0, Symbol.X));
        cells.add(new Cell(1, 1, Symbol.O));
        cells.add(new Cell(1, 2, Symbol.O));
        cells.add(new Cell(2, 0, Symbol.O));
        cells.add(new Cell(2, 1, Symbol.X));
        cells.add(new Cell(2, 2, Symbol.X));

        assertThat(winDetectorService.evaluate(cells, Symbol.X)).isEqualTo(GameStatus.DRAW);
    }
}
