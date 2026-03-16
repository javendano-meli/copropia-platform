package com.copropia.common.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Test
    @DisplayName("ok(data) debe crear respuesta exitosa")
    void ok_conData_creaRespuestaExitosa() {
        ApiResponse<String> response = ApiResponse.ok("test data");

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isEqualTo("test data");
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("ok(message, data) debe incluir mensaje")
    void ok_conMensaje_incluyeMensaje() {
        ApiResponse<Integer> response = ApiResponse.ok("Operacion exitosa", 42);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Operacion exitosa");
        assertThat(response.getData()).isEqualTo(42);
    }

    @Test
    @DisplayName("error debe crear respuesta fallida")
    void error_conMensaje_creaRespuestaFallida() {
        ApiResponse<Void> response = ApiResponse.error("Algo salio mal");

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Algo salio mal");
        assertThat(response.getData()).isNull();
    }
}
