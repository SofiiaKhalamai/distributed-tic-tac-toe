package com.khalamai.tictactoe.common.domain.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameStatusTest {

    @Test
    void shouldTreatOnlyInProgressAsUnfinished() {
        assertThat(GameStatus.IN_PROGRESS.isFinished()).isFalse();
        assertThat(GameStatus.X_WON.isFinished()).isTrue();
        assertThat(GameStatus.O_WON.isFinished()).isTrue();
        assertThat(GameStatus.DRAW.isFinished()).isTrue();
    }
}
