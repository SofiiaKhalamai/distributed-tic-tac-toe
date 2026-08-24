package com.khalamai.tictactoe.gameengine.service.mapper;

import com.khalamai.tictactoe.common.domain.enums.GameStatus;
import com.khalamai.tictactoe.common.domain.enums.Symbol;
import com.khalamai.tictactoe.gameengine.domain.entity.Game;
import com.khalamai.tictactoe.gameengine.dto.CellResponse;
import com.khalamai.tictactoe.gameengine.dto.GameResponse;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-24T15:24:19+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.12 (Azul Systems, Inc.)"
)
@Component
public class GameResponseMapperImpl implements GameResponseMapper {

    @Override
    public GameResponse toResponse(Game game) {
        if ( game == null ) {
            return null;
        }

        String gameId = null;
        GameStatus status = null;

        gameId = game.getGameId();
        status = game.getStatus();

        List<CellResponse> board = toCellResponses(game.getCells());
        Symbol nextTurn = game.getStatus().isFinished() ? null : game.getNextTurn();

        GameResponse gameResponse = new GameResponse( gameId, board, status, nextTurn );

        return gameResponse;
    }
}
