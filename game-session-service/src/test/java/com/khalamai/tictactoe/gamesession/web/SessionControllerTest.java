package com.khalamai.tictactoe.gamesession.web;

import com.khalamai.tictactoe.common.domain.enums.GameStatus;
import com.khalamai.tictactoe.gamesession.domain.enums.SessionStatus;
import com.khalamai.tictactoe.gamesession.domain.exception.SessionNotFoundException;
import com.khalamai.tictactoe.gamesession.dto.SessionResponse;
import com.khalamai.tictactoe.gamesession.service.SessionService;
import com.khalamai.tictactoe.gamesession.web.SessionController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SessionController.class)
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SessionService sessionService;

    @Test
    void shouldReturnCreatedForNewSession() throws Exception {
        when(sessionService.createSession())
                .thenReturn(new SessionResponse("s1", SessionStatus.CREATED, GameStatus.IN_PROGRESS, List.of(), List.of()));

        mockMvc.perform(post("/sessions"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void shouldReturnCompletedSessionAfterSimulate() throws Exception {
        when(sessionService.simulate("s1"))
                .thenReturn(new SessionResponse("s1", SessionStatus.COMPLETED, GameStatus.DRAW, List.of(), List.of()));

        mockMvc.perform(post("/sessions/s1/simulate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.gameStatus").value("DRAW"));
    }

    @Test
    void shouldReturnNotFoundForMissingSession() throws Exception {
        when(sessionService.getSession("missing")).thenThrow(new SessionNotFoundException("missing"));

        mockMvc.perform(get("/sessions/missing"))
                .andExpect(status().isNotFound());
    }
}
