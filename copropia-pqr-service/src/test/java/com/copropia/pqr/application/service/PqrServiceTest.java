package com.copropia.pqr.application.service;

import com.copropia.common.enums.EstadoPQR;
import com.copropia.common.enums.TipoPQR;
import com.copropia.common.exception.BusinessException;
import com.copropia.common.exception.ResourceNotFoundException;
import com.copropia.pqr.domain.model.Adjunto;
import com.copropia.pqr.domain.model.Pqr;
import com.copropia.pqr.domain.port.out.AdjuntoRepository;
import com.copropia.pqr.domain.port.out.ComentarioRepository;
import com.copropia.pqr.domain.port.out.PqrRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PqrServiceTest {

    @Mock private PqrRepository pqrRepository;
    @Mock private AdjuntoRepository adjuntoRepository;
    @Mock private ComentarioRepository comentarioRepository;
    @InjectMocks private PqrService pqrService;

    @Nested
    @DisplayName("Create Tests")
    class CreateTests {
        @Test
        @DisplayName("Debe crear PQR publica exitosamente")
        void create_publica_creaPqr() {
            Pqr pqr = Pqr.builder().copropiedadId(1L).usuarioId(1L).usuarioNombre("Juan")
                    .tipo(TipoPQR.QUEJA).titulo("Ruido excesivo").contenido("Hay mucho ruido")
                    .esPublico(true).build();
            when(pqrRepository.save(any())).thenAnswer(inv -> { Pqr p = inv.getArgument(0); p.setId(1L); return p; });

            Pqr result = pqrService.create(pqr);

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getEstado()).isEqualTo(EstadoPQR.ABIERTO);
            assertThat(result.getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Debe crear PQR con adjuntos")
        void create_conAdjuntos_guardaAdjuntos() {
            List<Adjunto> adjuntos = List.of(Adjunto.builder().url("http://img.com/1.jpg").tipoArchivo("IMAGEN").nombre("foto1.jpg").build());
            Pqr pqr = Pqr.builder().copropiedadId(1L).usuarioId(1L).usuarioNombre("Juan")
                    .tipo(TipoPQR.RECLAMO).titulo("Dano").contenido("Se dano la puerta")
                    .esPublico(true).adjuntos(adjuntos).build();
            when(pqrRepository.save(any())).thenAnswer(inv -> { Pqr p = inv.getArgument(0); p.setId(1L); return p; });
            when(adjuntoRepository.saveAll(anyList())).thenReturn(adjuntos);

            Pqr result = pqrService.create(pqr);

            assertThat(result.getAdjuntos()).hasSize(1);
            verify(adjuntoRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("Debe crear PQR privada")
        void create_privada_creaPqr() {
            Pqr pqr = Pqr.builder().copropiedadId(1L).usuarioId(1L).usuarioNombre("Maria")
                    .tipo(TipoPQR.PETICION).titulo("Solicitud").contenido("Necesito certificado")
                    .esPublico(false).build();
            when(pqrRepository.save(any())).thenAnswer(inv -> { Pqr p = inv.getArgument(0); p.setId(2L); return p; });

            Pqr result = pqrService.create(pqr);

            assertThat(result.isEsPublico()).isFalse();
            assertThat(result.getEstado()).isEqualTo(EstadoPQR.ABIERTO);
        }
    }

    @Nested
    @DisplayName("Estado Tests")
    class EstadoTests {
        @Test
        @DisplayName("Debe cambiar estado a EN_PROCESO")
        void cambiarEstado_abiertoAEnProceso_cambia() {
            Pqr pqr = Pqr.builder().id(1L).estado(EstadoPQR.ABIERTO).build();
            when(pqrRepository.findById(1L)).thenReturn(Optional.of(pqr));
            when(adjuntoRepository.findByPqrId(1L)).thenReturn(List.of());
            when(comentarioRepository.countByPqrId(1L)).thenReturn(0);
            when(pqrRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Pqr result = pqrService.cambiarEstado(1L, EstadoPQR.EN_PROCESO);

            assertThat(result.getEstado()).isEqualTo(EstadoPQR.EN_PROCESO);
        }

        @Test
        @DisplayName("No debe cambiar estado de PQR cerrada")
        void cambiarEstado_cerrada_lanzaExcepcion() {
            Pqr pqr = Pqr.builder().id(1L).estado(EstadoPQR.CERRADO).build();
            when(pqrRepository.findById(1L)).thenReturn(Optional.of(pqr));
            when(adjuntoRepository.findByPqrId(1L)).thenReturn(List.of());
            when(comentarioRepository.countByPqrId(1L)).thenReturn(0);

            assertThatThrownBy(() -> pqrService.cambiarEstado(1L, EstadoPQR.EN_PROCESO))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("cerrada");
        }

        @Test
        @DisplayName("PQR resuelta solo puede ir a CERRADO")
        void cambiarEstado_resueltaAAbierto_lanzaExcepcion() {
            Pqr pqr = Pqr.builder().id(1L).estado(EstadoPQR.RESUELTO).build();
            when(pqrRepository.findById(1L)).thenReturn(Optional.of(pqr));
            when(adjuntoRepository.findByPqrId(1L)).thenReturn(List.of());
            when(comentarioRepository.countByPqrId(1L)).thenReturn(0);

            assertThatThrownBy(() -> pqrService.cambiarEstado(1L, EstadoPQR.ABIERTO))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("CERRADO");
        }
    }

    @Test
    @DisplayName("Debe obtener PQR por ID con adjuntos y conteo comentarios")
    void getById_existente_retornaPqrCompleta() {
        Pqr pqr = Pqr.builder().id(1L).titulo("Test").build();
        when(pqrRepository.findById(1L)).thenReturn(Optional.of(pqr));
        when(adjuntoRepository.findByPqrId(1L)).thenReturn(List.of(Adjunto.builder().id(1L).url("http://img.com/1.jpg").build()));
        when(comentarioRepository.countByPqrId(1L)).thenReturn(5);

        Pqr result = pqrService.getById(1L);

        assertThat(result.getAdjuntos()).hasSize(1);
        assertThat(result.getCantidadComentarios()).isEqualTo(5);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando PQR no existe")
    void getById_noExiste_lanzaExcepcion() {
        when(pqrRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> pqrService.getById(99L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Debe obtener feed publico")
    void getFeedPublico_retornaListaPublica() {
        when(pqrRepository.findByCopropiedadIdAndEsPublicoTrue(1L)).thenReturn(List.of(
                Pqr.builder().id(1L).esPublico(true).build(),
                Pqr.builder().id(2L).esPublico(true).build()
        ));
        when(adjuntoRepository.findByPqrId(any())).thenReturn(List.of());
        when(comentarioRepository.countByPqrId(any())).thenReturn(0);

        assertThat(pqrService.getFeedPublico(1L)).hasSize(2);
    }
}
