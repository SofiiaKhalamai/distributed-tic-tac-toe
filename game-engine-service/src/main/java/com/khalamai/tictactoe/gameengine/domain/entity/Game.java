package com.khalamai.tictactoe.gameengine.domain.entity;

import com.khalamai.tictactoe.common.domain.enums.GameStatus;
import com.khalamai.tictactoe.common.domain.enums.Symbol;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Game {

    @Id
    private String gameId;

    @ElementCollection
    @CollectionTable(name = "game_cells", joinColumns = @JoinColumn(name = "game_id"))
    private Set<Cell> cells = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameStatus status = GameStatus.IN_PROGRESS;

    @Enumerated(EnumType.STRING)
    private Symbol nextTurn = Symbol.X;

    @Setter(AccessLevel.NONE)
    @Version
    private Long version;

    public Game(String gameId) {
        this.gameId = gameId;
    }
}
