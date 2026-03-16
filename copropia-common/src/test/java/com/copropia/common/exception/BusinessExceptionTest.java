package com.copropia.common.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessExceptionTest {

    @Test
    @DisplayName("Constructor con solo mensaje debe usar BAD_REQUEST")
    void constructor_soloMensaje_usaBadRequest() {
        BusinessException ex = new BusinessException("Error de negocio");

        assertThat(ex.getMessage()).isEqualTo("Error de negocio");
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Constructor con status debe usar status especificado")
    void constructor_conStatus_usaStatusEspecificado() {
        BusinessException ex = new BusinessException("No autorizado", HttpStatus.UNAUTHORIZED);

        assertThat(ex.getMessage()).isEqualTo("No autorizado");
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
