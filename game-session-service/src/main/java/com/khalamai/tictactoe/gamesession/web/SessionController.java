package com.khalamai.tictactoe.gamesession.web;

import com.khalamai.tictactoe.gamesession.dto.SessionResponse;
import com.khalamai.tictactoe.gamesession.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SessionResponse createSession() {
        return sessionService.createSession();
    }

    @PostMapping("/{sessionId}/simulate")
    public SessionResponse simulate(@PathVariable String sessionId) {
        return sessionService.simulate(sessionId);
    }

    @GetMapping("/{sessionId}")
    public SessionResponse getSession(@PathVariable String sessionId) {
        return sessionService.getSession(sessionId);
    }
}
