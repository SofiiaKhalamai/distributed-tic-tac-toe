package com.khalamai.tictactoe.gameengine.service;

import com.khalamai.tictactoe.gameengine.domain.exception.GameAlreadyFinishedException;
import com.khalamai.tictactoe.gameengine.domain.exception.GameNotFoundException;
import com.khalamai.tictactoe.gameengine.domain.exception.InvalidMoveException;
import com.khalamai.tictactoe.gameengine.dto.GameResponse;
import com.khalamai.tictactoe.gameengine.dto.MoveRequest;

public interface GameService {

    GameResponse getGame(String gameId);

    GameResponse applyMove(String gameId, MoveRequest request);
}
