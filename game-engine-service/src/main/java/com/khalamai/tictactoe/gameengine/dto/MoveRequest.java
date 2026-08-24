package com.khalamai.tictactoe.gameengine.dto;

import com.khalamai.tictactoe.common.domain.enums.Symbol;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MoveRequest(
        @NotNull Symbol symbol,
        @Min(0) @Max(2) int row,
        @Min(0) @Max(2) int col
) {
}
