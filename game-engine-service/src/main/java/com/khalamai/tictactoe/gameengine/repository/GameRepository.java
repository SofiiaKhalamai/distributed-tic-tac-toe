package com.khalamai.tictactoe.gameengine.repository;

import com.khalamai.tictactoe.gameengine.domain.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, String> {
}
