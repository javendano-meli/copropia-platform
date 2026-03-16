package com.copropia.common.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private static final String SECRET = "test-secret-key-minimum-32-characters-long-for-hmac-sha-256";
    private static final long EXPIRATION = 3600000; // 1 hora

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(SECRET, EXPIRATION);
    }

    @Test
    @DisplayName("Debe generar token valido")
    void generateToken_datosValidos_generaToken() {
        String token = jwtUtils.generateToken("juan@test.com", "PROPIETARIO", 1L);

        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("Debe extraer email del token")
    void getEmail_tokenValido_retornaEmail() {
        String token = jwtUtils.generateToken("juan@test.com", "PROPIETARIO", 1L);

        assertThat(jwtUtils.getEmail(token)).isEqualTo("juan@test.com");
    }

    @Test
    @DisplayName("Debe extraer rol del token")
    void getRol_tokenValido_retornaRol() {
        String token = jwtUtils.generateToken("juan@test.com", "ADMIN_COPROPIEDAD", 1L);

        assertThat(jwtUtils.getRol(token)).isEqualTo("ADMIN_COPROPIEDAD");
    }

    @Test
    @DisplayName("Debe extraer copropiedadId del token")
    void getCopropiedadId_tokenValido_retornaCopropiedadId() {
        String token = jwtUtils.generateToken("juan@test.com", "PROPIETARIO", 5L);

        assertThat(jwtUtils.getCopropiedadId(token)).isEqualTo(5L);
    }

    @Test
    @DisplayName("Token debe ser valido antes de expirar")
    void isTokenValid_tokenNoExpirado_retornaTrue() {
        String token = jwtUtils.generateToken("juan@test.com", "PROPIETARIO", 1L);

        assertThat(jwtUtils.isTokenValid(token)).isTrue();
    }

    @Test
    @DisplayName("Token expirado debe ser invalido")
    void isTokenValid_tokenExpirado_retornaFalse() {
        JwtUtils expiredJwt = new JwtUtils(SECRET, 0); // expira inmediatamente
        String token = expiredJwt.generateToken("juan@test.com", "PROPIETARIO", 1L);

        // Small delay to ensure token is expired
        try { Thread.sleep(10); } catch (InterruptedException ignored) {}

        assertThat(expiredJwt.isTokenValid(token)).isFalse();
    }

    @Test
    @DisplayName("Token invalido debe retornar false")
    void isTokenValid_tokenInvalido_retornaFalse() {
        assertThat(jwtUtils.isTokenValid("invalid-token")).isFalse();
    }

    @Test
    @DisplayName("Debe manejar copropiedadId null")
    void generateToken_copropiedadIdNull_generaToken() {
        String token = jwtUtils.generateToken("admin@test.com", "SUPER_ADMIN", null);

        assertThat(token).isNotNull();
        assertThat(jwtUtils.getEmail(token)).isEqualTo("admin@test.com");
        assertThat(jwtUtils.getRol(token)).isEqualTo("SUPER_ADMIN");
    }
}
