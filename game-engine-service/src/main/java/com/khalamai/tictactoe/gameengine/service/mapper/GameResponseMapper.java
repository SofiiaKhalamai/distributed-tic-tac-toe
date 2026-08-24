package com.khalamai.tictactoe.gameengine.service.mapper;

import com.khalamai.tictactoe.gameengine.domain.entity.Cell;
import com.khalamai.tictactoe.gameengine.domain.entity.Game;
import com.khalamai.tictactoe.gameengine.dto.CellResponse;
import com.khalamai.tictactoe.gameengine.dto.GameResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface GameResponseMapper {

    @Mapping(target = "board", expression = "java(toCellResponses(game.getCells()))")
    @Mapping(target = "nextTurn", expression = "java(game.getStatus().isFinished() ? null : game.getNextTurn())")
    GameResponse toResponse(Game game);

    default List<CellResponse> toCellResponses(Set<Cell> cells) {
        return cells.stream()
                .map(cell -> new CellResponse(cell.row(), cell.col(), cell.symbol()))
                .toList();
    }
}
