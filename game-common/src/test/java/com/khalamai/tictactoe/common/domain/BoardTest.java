package com.khalamai.tictactoe.common.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BoardTest {

    @Test
    void shouldExposeTicTacToeBoardDimensions() {
        assertThat(Board.SIZE).isEqualTo(3);
        assertThat(Board.CELL_COUNT).isEqualTo(9);
    }
}
