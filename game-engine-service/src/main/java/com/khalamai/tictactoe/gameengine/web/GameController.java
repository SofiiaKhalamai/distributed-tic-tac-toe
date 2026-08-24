package com.khalamai.tictactoe.gameengine.web;

import com.khalamai.tictactoe.gameengine.dto.GameResponse;
import com.khalamai.tictactoe.gameengine.dto.MoveRequest;
import com.khalamai.tictactoe.gameengine.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping("/{gameId}/move")
    public GameResponse makeMove(@PathVariable String gameId, @Valid @RequestBody MoveRequest request) {
        return gameService.applyMove(gameId, request);
    }

    @GetMapping("/{gameId}")
    public GameResponse getGame(@PathVariable String gameId) {
        return gameService.getGame(gameId);
    }
}
