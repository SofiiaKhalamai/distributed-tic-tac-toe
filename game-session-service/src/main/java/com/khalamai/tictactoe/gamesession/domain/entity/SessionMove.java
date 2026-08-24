package com.khalamai.tictactoe.gamesession.domain.entity;

import com.khalamai.tictactoe.common.domain.enums.Symbol;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "session_moves")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SessionMove {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Session session;

    private int sequence;

    @Enumerated(EnumType.STRING)
    private Symbol symbol;

    @Column(name = "board_row")
    private int row;

    @Column(name = "board_col")
    private int col;

    public SessionMove(Session session, Symbol symbol, int row, int col) {
        this.session = session;
        // relies on being constructed before it's added to session.getMoveHistory()
        this.sequence = session.getMoveHistory().size() + 1;
        this.symbol = symbol;
        this.row = row;
        this.col = col;
    }
}
