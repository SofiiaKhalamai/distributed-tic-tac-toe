package com.khalamai.tictactoe.gamesession.repository;

import com.khalamai.tictactoe.gamesession.domain.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, String> {
}
