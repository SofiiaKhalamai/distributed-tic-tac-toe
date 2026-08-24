package com.khalamai.tictactoe.gamesession.support;

import com.khalamai.tictactoe.common.domain.enums.Symbol;
import com.khalamai.tictactoe.gamesession.client.EngineCell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class EngineBoardFixtures {

    private EngineBoardFixtures() {
    }

    public static List<Symbol> emptyBoard() {
        return new ArrayList<>(Arrays.asList(null, null, null, null, null, null, null, null, null));
    }

    // converts a flat, row-major board (index = row*3+col) into the engine's sparse wire format.
    public static List<EngineCell> toSparse(List<Symbol> board) {
        var cells = new ArrayList<EngineCell>();
        for (var i = 0; i < board.size(); i++) {
            if (Objects.nonNull(board.get(i))) {
                cells.add(new EngineCell(i / 3, i % 3, board.get(i)));
            }
        }
        return cells;
    }
}
