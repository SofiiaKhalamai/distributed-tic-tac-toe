package com.khalamai.tictactoe.gameengine.service.impl;

import com.khalamai.tictactoe.gameengine.domain.entity.Cell;
import com.khalamai.tictactoe.common.domain.enums.Symbol;
import com.khalamai.tictactoe.gameengine.service.BoardService;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
public class BoardServiceImpl implements BoardService {

    @Override
    public boolean isOccupied(Set<Cell> cells, int row, int col) {
        return findCell(cells, row, col).isPresent();
    }

    @Override
    public void place(Set<Cell> cells, Symbol symbol, int row, int col) {
        cells.add(new Cell(row, col, symbol));
    }

    @Override
    public Symbol at(Set<Cell> cells, int row, int col) {
        return findCell(cells, row, col).map(Cell::symbol).orElse(null);
    }

    private Optional<Cell> findCell(Set<Cell> cells, int row, int col) {
        return cells.stream()
                .filter(cell -> cell.row() == row && cell.col() == col)
                .findFirst();
    }
}
