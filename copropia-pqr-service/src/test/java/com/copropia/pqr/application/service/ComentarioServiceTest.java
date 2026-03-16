package com.copropia.pqr.application.service;

import com.copropia.common.enums.EstadoPQR;
import com.copropia.common.exception.BusinessException;
import com.copropia.common.exception.ResourceNotFoundException;
import com.copropia.pqr.domain.model.Comentario;
import com.copropia.pqr.domain.model.Pqr;
import com.copropia.pqr.domain.port.out.ComentarioRepository;
import com.copropia.pqr.domain.port.out.PqrRepository;
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
class ComentarioServiceTest {

    @Mock private ComentarioRepository comentarioRepository;
    @Mock private PqrRepository pqrRepository;
    @InjectMocks private ComentarioService comentarioService;

    @Test
    @DisplayName("Debe crear comentario en PQR abierta")
    void create_pqrAbierta_creaComentario() {
        Pqr pqr = Pqr.builder().id(1L).estado(EstadoPQR.ABIERTO).esPublico(true).build();
        Comentario comentario = Comentario.builder().pqrId(1L).usuarioId(1L).usuarioNombre("Juan").contenido("De acuerdo").build();
        when(pqrRepository.findById(1L)).thenReturn(Optional.of(pqr));
        when(comentarioRepository.save(any())).thenAnswer(inv -> { Comentario c = inv.getArgument(0); c.setId(1L); return c; });

        Comentario result = comentarioService.create(comentario);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("No debe comentar en PQR cerrada")
    void create_pqrCerrada_lanzaExcepcion() {
        Pqr pqr = Pqr.builder().id(1L).estado(EstadoPQR.CERRADO).build();
        Comentario comentario = Comentario.builder().pqrId(1L).build();
        when(pqrRepository.findById(1L)).thenReturn(Optional.of(pqr));

        assertThatThrownBy(() -> comentarioService.create(comentario))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("cerrada");
    }

    @Test
    @DisplayName("Debe lanzar excepcion si PQR no existe")
    void create_pqrNoExiste_lanzaExcepcion() {
        Comentario comentario = Comentario.builder().pqrId(99L).build();
        when(pqrRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> comentarioService.create(comentario))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Debe obtener comentarios por PQR")
    void getByPqrId_retornaLista() {
        when(comentarioRepository.findByPqrId(1L)).thenReturn(List.of(
                Comentario.builder().id(1L).contenido("Hola").build(),
                Comentario.builder().id(2L).contenido("Gracias").build()
        ));

        assertThat(comentarioService.getByPqrId(1L)).hasSize(2);
    }
}
