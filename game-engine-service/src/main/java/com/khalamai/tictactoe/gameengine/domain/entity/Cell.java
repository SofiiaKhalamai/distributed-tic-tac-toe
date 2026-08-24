package com.khalamai.tictactoe.gameengine.domain.entity;

import com.khalamai.tictactoe.common.domain.enums.Symbol;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public record Cell(
        @Column(name = "board_row") int row,
        @Column(name = "board_col") int col,
        @Enumerated(EnumType.STRING) Symbol symbol
) {
}
