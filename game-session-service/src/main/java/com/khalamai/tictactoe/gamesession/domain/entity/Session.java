package com.khalamai.tictactoe.gamesession.domain.entity;

import com.khalamai.tictactoe.common.domain.enums.GameStatus;
import com.khalamai.tictactoe.gamesession.domain.enums.SessionStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "game_sessions")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Session {

    @Id
    @Setter(AccessLevel.NONE)
    private String sessionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status = SessionStatus.CREATED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameStatus gameStatus = GameStatus.IN_PROGRESS;

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sequence")
    private List<SessionMove> moveHistory = new ArrayList<>();

    public Session(String sessionId) {
        this.sessionId = sessionId;
    }
}
