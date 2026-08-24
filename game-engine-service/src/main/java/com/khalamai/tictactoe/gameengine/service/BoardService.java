package com.khalamai.tictactoe.gameengine.service;

import com.khalamai.tictactoe.gameengine.domain.entity.Cell;
import com.khalamai.tictactoe.common.domain.enums.Symbol;

import java.util.Set;

public interface BoardService {

    boolean isOccupied(Set<Cell> cells, int row, int col);

    void place(Set<Cell> cells, Symbol symbol, int row, int col);

    Symbol at(Set<Cell> cells, int row, int col);
}
