package com.copropia.auth.application.service;

import com.copropia.auth.domain.model.Usuario;
import com.copropia.auth.domain.port.out.UsuarioRepository;
import com.copropia.common.enums.Rol;
import com.copropia.common.exception.BusinessException;
import com.copropia.common.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    private Usuario testUsuario;

    @BeforeEach
    void setUp() {
        testUsuario = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .email("juan@test.com")
                .passwordHash("hashedPassword")
                .rol(Rol.PROPIETARIO)
                .copropiedadId(1L)
                .activo(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Debe retornar token cuando credenciales son validas")
        void login_credencialesValidas_retornaToken() {
            when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(testUsuario));
            when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
            when(jwtUtils.generateToken("juan@test.com", "PROPIETARIO", 1L)).thenReturn("jwt-token");

            String token = authService.login("juan@test.com", "password123");

            assertThat(token).isEqualTo("jwt-token");
            verify(usuarioRepository).findByEmail("juan@test.com");
            verify(passwordEncoder).matches("password123", "hashedPassword");
        }

        @Test
        @DisplayName("Debe lanzar excepcion cuando email no existe")
        void login_emailNoExiste_lanzaExcepcion() {
            when(usuarioRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login("noexiste@test.com", "password"))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Credenciales invalidas");
        }

        @Test
        @DisplayName("Debe lanzar excepcion cuando password es incorrecta")
        void login_passwordIncorrecta_lanzaExcepcion() {
            when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(testUsuario));
            when(passwordEncoder.matches("wrongpass", "hashedPassword")).thenReturn(false);

            assertThatThrownBy(() -> authService.login("juan@test.com", "wrongpass"))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Credenciales invalidas");
        }

        @Test
        @DisplayName("Debe lanzar excepcion cuando usuario esta inactivo")
        void login_usuarioInactivo_lanzaExcepcion() {
            testUsuario.setActivo(false);
            when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(testUsuario));

            assertThatThrownBy(() -> authService.login("juan@test.com", "password"))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("inactivo");
        }
    }

    @Nested
    @DisplayName("Register Tests")
    class RegisterTests {

        @Test
        @DisplayName("Debe registrar usuario exitosamente")
        void register_datosValidos_registraUsuario() {
            Usuario newUsuario = Usuario.builder()
                    .nombre("Maria")
                    .apellido("Lopez")
                    .email("maria@test.com")
                    .rol(Rol.PROPIETARIO)
                    .copropiedadId(1L)
                    .build();

            when(usuarioRepository.existsByEmail("maria@test.com")).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("encodedPass");
            when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> {
                Usuario u = inv.getArgument(0);
                u.setId(2L);
                return u;
            });

            Usuario result = authService.register(newUsuario, "password123");

            assertThat(result.getId()).isEqualTo(2L);
            assertThat(result.getPasswordHash()).isEqualTo("encodedPass");
            assertThat(result.isActivo()).isTrue();
            assertThat(result.getCreatedAt()).isNotNull();
            verify(usuarioRepository).save(any(Usuario.class));
        }

        @Test
        @DisplayName("Debe lanzar excepcion cuando email ya existe")
        void register_emailDuplicado_lanzaExcepcion() {
            Usuario newUsuario = Usuario.builder()
                    .email("existente@test.com")
                    .build();

            when(usuarioRepository.existsByEmail("existente@test.com")).thenReturn(true);

            assertThatThrownBy(() -> authService.register(newUsuario, "password"))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("ya esta registrado");

            verify(usuarioRepository, never()).save(any());
        }
    }
}
