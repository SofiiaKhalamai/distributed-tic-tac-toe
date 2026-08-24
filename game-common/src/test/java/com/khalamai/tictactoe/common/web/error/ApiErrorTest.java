package com.khalamai.tictactoe.common.web.error;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ApiErrorTest {

    @Test
    void shouldBuildFromHttpStatus() {
        var error = ApiError.of(HttpStatus.NOT_FOUND, "Session not found: s1");

        assertThat(error.status()).isEqualTo(404);
        assertThat(error.error()).isEqualTo("Not Found");
        assertThat(error.message()).isEqualTo("Session not found: s1");
        assertThat(error.timestamp()).isNotNull();
    }
}
