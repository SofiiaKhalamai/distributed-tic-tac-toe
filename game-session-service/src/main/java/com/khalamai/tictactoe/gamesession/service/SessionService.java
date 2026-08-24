package com.khalamai.tictactoe.gamesession.service;

import com.khalamai.tictactoe.gamesession.domain.exception.GameEngineCommunicationException;
import com.khalamai.tictactoe.gamesession.domain.exception.SessionAlreadySimulatedException;
import com.khalamai.tictactoe.gamesession.domain.exception.SessionNotFoundException;
import com.khalamai.tictactoe.gamesession.dto.SessionResponse;

public interface SessionService {

    SessionResponse createSession();

    SessionResponse simulate(String sessionId);

    SessionResponse getSession(String sessionId);
}
