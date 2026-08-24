package com.khalamai.tictactoe.gamesession.service.mapper;

import com.khalamai.tictactoe.common.domain.enums.GameStatus;
import com.khalamai.tictactoe.common.domain.enums.Symbol;
import com.khalamai.tictactoe.gamesession.domain.entity.Session;
import com.khalamai.tictactoe.gamesession.domain.entity.SessionMove;
import com.khalamai.tictactoe.gamesession.domain.enums.SessionStatus;
import com.khalamai.tictactoe.gamesession.dto.CellResponse;
import com.khalamai.tictactoe.gamesession.dto.MoveRecordResponse;
import com.khalamai.tictactoe.gamesession.dto.SessionResponse;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-24T15:24:26+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.12 (Azul Systems, Inc.)"
)
@Component
public class SessionResponseMapperImpl implements SessionResponseMapper {

    @Override
    public SessionResponse toResponse(Session session) {
        if ( session == null ) {
            return null;
        }

        String sessionId = null;
        SessionStatus status = null;
        GameStatus gameStatus = null;
        List<MoveRecordResponse> moveHistory = null;

        sessionId = session.getSessionId();
        status = session.getStatus();
        gameStatus = session.getGameStatus();
        moveHistory = sessionMoveListToMoveRecordResponseList( session.getMoveHistory() );

        List<CellResponse> board = toBoard(session.getMoveHistory());

        SessionResponse sessionResponse = new SessionResponse( sessionId, status, gameStatus, board, moveHistory );

        return sessionResponse;
    }

    protected MoveRecordResponse sessionMoveToMoveRecordResponse(SessionMove sessionMove) {
        if ( sessionMove == null ) {
            return null;
        }

        int sequence = 0;
        Symbol symbol = null;
        int row = 0;
        int col = 0;

        sequence = sessionMove.getSequence();
        symbol = sessionMove.getSymbol();
        row = sessionMove.getRow();
        col = sessionMove.getCol();

        MoveRecordResponse moveRecordResponse = new MoveRecordResponse( sequence, symbol, row, col );

        return moveRecordResponse;
    }

    protected List<MoveRecordResponse> sessionMoveListToMoveRecordResponseList(List<SessionMove> list) {
        if ( list == null ) {
            return null;
        }

        List<MoveRecordResponse> list1 = new ArrayList<MoveRecordResponse>( list.size() );
        for ( SessionMove sessionMove : list ) {
            list1.add( sessionMoveToMoveRecordResponse( sessionMove ) );
        }

        return list1;
    }
}
