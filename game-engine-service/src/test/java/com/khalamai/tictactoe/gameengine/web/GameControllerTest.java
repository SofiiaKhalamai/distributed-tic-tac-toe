package com.khalamai.tictactoe.gameengine.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khalamai.tictactoe.common.domain.enums.GameStatus;
import com.khalamai.tictactoe.common.domain.enums.Symbol;
import com.khalamai.tictactoe.gameengine.domain.exception.GameNotFoundException;
import com.khalamai.tictactoe.gameengine.dto.GameResponse;
import com.khalamai.tictactoe.gameengine.dto.MoveRequest;
import com.khalamai.tictactoe.gameengine.service.GameService;
import com.khalamai.tictactoe.gameengine.web.GameController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GameService gameService;

    @Test
    void shouldReturnGameStateAfterMove() throws Exception {
        when(gameService.applyMove(eq("g1"), any(MoveRequest.class)))
                .thenReturn(new GameResponse("g1", Collections.emptyList(), GameStatus.IN_PROGRESS, Symbol.O));

        mockMvc.perform(post("/games/g1/move")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new MoveRequest(Symbol.X, 0, 0))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nextTurn").value("O"));
    }

    @Test
    void shouldRejectMoveWithOutOfRangeCoordinates() throws Exception {
        mockMvc.perform(post("/games/g1/move")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new MoveRequest(Symbol.X, 5, 0))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundForMissingGame() throws Exception {
        when(gameService.getGame("missing")).thenThrow(new GameNotFoundException("missing"));

        mockMvc.perform(get("/games/missing"))
                .andExpect(status().isNotFound());
    }
}
