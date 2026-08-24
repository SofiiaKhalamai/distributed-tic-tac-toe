package com.khalamai.tictactoe.gamesession.service.mapper;

import com.khalamai.tictactoe.gamesession.domain.entity.Session;
import com.khalamai.tictactoe.gamesession.domain.entity.SessionMove;
import com.khalamai.tictactoe.gamesession.dto.CellResponse;
import com.khalamai.tictactoe.gamesession.dto.SessionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SessionResponseMapper {

    @Mapping(target = "board", expression = "java(toBoard(session.getMoveHistory()))")
    SessionResponse toResponse(Session session);

    // derived from moveHistory instead of a stored board column, to avoid duplicated state.
    default List<CellResponse> toBoard(List<SessionMove> moveHistory) {
        return moveHistory.stream()
                .map(move -> new CellResponse(move.getRow(), move.getCol(), move.getSymbol()))
                .toList();
    }
}
