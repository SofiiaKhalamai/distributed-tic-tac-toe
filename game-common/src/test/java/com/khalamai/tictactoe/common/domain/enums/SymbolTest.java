package com.khalamai.tictactoe.common.domain.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SymbolTest {

    @Test
    void shouldReturnTheOtherSymbolAsOpponent() {
        assertThat(Symbol.X.opponent()).isEqualTo(Symbol.O);
        assertThat(Symbol.O.opponent()).isEqualTo(Symbol.X);
    }
}
