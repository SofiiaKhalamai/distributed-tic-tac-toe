package com.khalamai.tictactoe.gamesession.service;

import java.util.List;

public interface MoveGeneratorService {

    Cell nextMove(List<Cell> occupiedCells);

    record Cell(int row, int col) {
    }
}
