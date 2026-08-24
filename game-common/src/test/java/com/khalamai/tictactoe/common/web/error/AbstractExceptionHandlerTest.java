package com.khalamai.tictactoe.common.web.error;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class AbstractExceptionHandlerTest {

    private final AbstractExceptionHandler handler = new AbstractExceptionHandler() {};

    @Test
    void shouldReturnFirstFieldErrorMessageOnValidationFailure() {
        var bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "row", "must be between 0 and 2"));
        var ex = new MethodArgumentNotValidException(mock(MethodParameter.class), bindingResult);

        var response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().message()).isEqualTo("row must be between 0 and 2");
    }

    @Test
    void shouldFallBackToGenericMessageWhenNoFieldErrors() {
        var bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        var ex = new MethodArgumentNotValidException(mock(MethodParameter.class), bindingResult);

        var response = handler.handleValidation(ex);

        assertThat(response.getBody().message()).isEqualTo("Invalid request");
    }

    @Test
    void shouldReturnGenericMessageForUnexpectedExceptionsWithoutLeakingDetails() {
        var response = handler.handleUnexpected(new RuntimeException("db password is hunter2"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().message()).isEqualTo("Unexpected server error");
        assertThat(response.getBody().message()).doesNotContain("hunter2");
    }
}
