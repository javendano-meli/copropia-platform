package com.copropia.auth.application.service;

import com.copropia.auth.domain.model.Usuario;
import com.copropia.auth.domain.port.out.UsuarioRepository;
import com.copropia.common.enums.Rol;
import com.copropia.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario testUsuario;

    @BeforeEach
    void setUp() {
        testUsuario = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .email("juan@test.com")
                .rol(Rol.PROPIETARIO)
                .copropiedadId(1L)
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("Debe obtener usuario por ID")
    void getById_existente_retornaUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(testUsuario));

        Usuario result = usuarioService.getById(1L);

        assertThat(result.getNombre()).isEqualTo("Juan");
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando usuario no existe")
    void getById_noExistente_lanzaExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Debe obtener usuarios por copropiedad")
    void getByCopropiedadId_retornaLista() {
        when(usuarioRepository.findByCopropiedadId(1L)).thenReturn(List.of(testUsuario));

        List<Usuario> result = usuarioService.getByCopropiedadId(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Debe actualizar usuario")
    void update_datosValidos_actualizaUsuario() {
        Usuario updated = Usuario.builder().nombre("Juan Carlos").apellido("Perez Garcia").telefono("3001234567").build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(testUsuario));
        when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Usuario result = usuarioService.update(1L, updated);

        assertThat(result.getNombre()).isEqualTo("Juan Carlos");
        assertThat(result.getApellido()).isEqualTo("Perez Garcia");
    }

    @Test
    @DisplayName("Debe desactivar usuario")
    void deactivate_desactivaUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(testUsuario));
        when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        usuarioService.deactivate(1L);

        assertThat(testUsuario.isActivo()).isFalse();
        verify(usuarioRepository).save(testUsuario);
    }
}
