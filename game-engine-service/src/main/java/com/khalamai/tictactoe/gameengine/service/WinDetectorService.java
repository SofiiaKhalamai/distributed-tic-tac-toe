package com.khalamai.tictactoe.gameengine.service;

import com.khalamai.tictactoe.gameengine.domain.entity.Cell;
import com.khalamai.tictactoe.common.domain.enums.GameStatus;
import com.khalamai.tictactoe.common.domain.enums.Symbol;

import java.util.Set;

public interface WinDetectorService {

    GameStatus evaluate(Set<Cell> cells, Symbol justMoved);
}
