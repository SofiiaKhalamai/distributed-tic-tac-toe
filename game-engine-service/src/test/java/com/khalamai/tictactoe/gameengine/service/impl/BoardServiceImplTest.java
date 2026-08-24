package com.khalamai.tictactoe.gameengine.service.impl;

import com.khalamai.tictactoe.gameengine.domain.entity.Cell;
import com.khalamai.tictactoe.common.domain.enums.Symbol;
import com.khalamai.tictactoe.gameengine.service.BoardService;
import com.khalamai.tictactoe.gameengine.service.impl.BoardServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BoardServiceImplTest {

    private final BoardService boardService = new BoardServiceImpl();

    @Test
    void shouldReportEmptyCellAsUnoccupied() {
        Set<Cell> cells = new HashSet<>();

        assertThat(boardService.isOccupied(cells, 0, 0)).isFalse();
        assertThat(boardService.at(cells, 0, 0)).isNull();
    }

    @Test
    void shouldPlaceAndReadBackSymbol() {
        Set<Cell> cells = new HashSet<>();

        boardService.place(cells, Symbol.X, 1, 2);

        assertThat(boardService.isOccupied(cells, 1, 2)).isTrue();
        assertThat(boardService.at(cells, 1, 2)).isEqualTo(Symbol.X);
        assertThat(boardService.isOccupied(cells, 0, 0)).isFalse();
    }
}
