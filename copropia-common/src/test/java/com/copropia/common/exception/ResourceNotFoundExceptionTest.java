package com.copropia.common.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceNotFoundExceptionTest {

    @Test
    @DisplayName("Debe generar mensaje con recurso e ID")
    void constructor_recursoYId_generaMensaje() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Usuario", 42L);

        assertThat(ex.getMessage()).contains("Usuario").contains("42");
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
