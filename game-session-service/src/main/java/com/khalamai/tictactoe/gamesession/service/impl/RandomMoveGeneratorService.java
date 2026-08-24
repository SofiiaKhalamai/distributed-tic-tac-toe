package com.khalamai.tictactoe.gamesession.service.impl;

import com.khalamai.tictactoe.gamesession.service.MoveGeneratorService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

@Component
public class RandomMoveGeneratorService implements MoveGeneratorService {

    private static final int SIZE = 3;

    private static final List<Cell> ALL_CELLS = IntStream.range(0, SIZE)
            .boxed()
            .flatMap(row -> IntStream.range(0, SIZE).mapToObj(col -> new Cell(row, col)))
            .toList();

    private final Random random = new Random();

    @Override
    public Cell nextMove(List<Cell> occupiedCells) {
        var occupied = Set.copyOf(occupiedCells);
        var emptyCells = ALL_CELLS.stream()
                .filter(cell -> !occupied.contains(cell))
                .toList();

        if (emptyCells.isEmpty()) {
            throw new IllegalStateException("No empty cells left to move into");
        }
        return emptyCells.get(random.nextInt(emptyCells.size()));
    }
}
