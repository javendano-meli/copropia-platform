package com.copropia.auth.infrastructure.adapter.in.web;

import com.copropia.auth.domain.model.Usuario;
import com.copropia.auth.domain.port.in.AuthUseCase;
import com.copropia.common.dto.ApiResponse;
import com.copropia.common.enums.Rol;
import com.copropia.auth.infrastructure.adapter.in.web.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthUseCase authUseCase;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("POST /api/auth/login - debe retornar token")
    void login_credencialesValidas_retorna200() {
        when(authUseCase.login("juan@test.com", "password123")).thenReturn("jwt-token-123");

        LoginRequest request = new LoginRequest();
        request.setEmail("juan@test.com");
        request.setPassword("password123");

        ResponseEntity<ApiResponse<LoginResponse>> response = authController.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData().getToken()).isEqualTo("jwt-token-123");
    }

    @Test
    @DisplayName("POST /api/auth/register - debe registrar usuario")
    void register_datosValidos_retorna201() {
        Usuario created = Usuario.builder()
                .id(1L).nombre("Juan").apellido("Perez")
                .email("juan@test.com").rol(Rol.PROPIETARIO)
                .copropiedadId(1L).activo(true).build();

        when(authUseCase.register(any(Usuario.class), anyString())).thenReturn(created);

        RegisterRequest request = new RegisterRequest();
        request.setNombre("Juan");
        request.setApellido("Perez");
        request.setEmail("juan@test.com");
        request.setPassword("password123");
        request.setRol(Rol.PROPIETARIO);
        request.setCopropiedadId(1L);

        ResponseEntity<ApiResponse<UsuarioResponse>> response = authController.register(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData().getId()).isEqualTo(1L);
        assertThat(response.getBody().getData().getNombre()).isEqualTo("Juan");
    }
}
