package com.copropia.asamblea.application.service;

import com.copropia.asamblea.domain.model.Asamblea;
import com.copropia.asamblea.domain.model.OpcionVoto;
import com.copropia.asamblea.domain.model.Votacion;
import com.copropia.asamblea.domain.port.out.AsambleaRepository;
import com.copropia.asamblea.domain.port.out.OpcionVotoRepository;
import com.copropia.asamblea.domain.port.out.VotacionRepository;
import com.copropia.common.enums.EstadoAsamblea;
import com.copropia.common.enums.EstadoVotacion;
import com.copropia.common.enums.TipoVotacion;
import com.copropia.common.exception.BusinessException;
import com.copropia.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
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
class VotacionServiceTest {

    @Mock
    private VotacionRepository votacionRepository;
    @Mock
    private AsambleaRepository asambleaRepository;
    @Mock
    private OpcionVotoRepository opcionVotoRepository;

    @InjectMocks
    private VotacionService votacionService;

    private Votacion testVotacion;
    private Asamblea testAsamblea;

    @BeforeEach
    void setUp() {
        testAsamblea = Asamblea.builder()
                .id(1L).copropiedadId(1L).estado(EstadoAsamblea.ABIERTA).build();

        testVotacion = Votacion.builder()
                .id(1L).asambleaId(1L).titulo("Aprobacion presupuesto 2026")
                .tipoVotacion(TipoVotacion.APROBACION_SIMPLE)
                .estado(EstadoVotacion.PENDIENTE).orden(1).build();
    }

    @Nested
    @DisplayName("Create Tests")
    class CreateTests {

        @Test
        @DisplayName("Debe crear votacion con opciones")
        void create_conOpciones_creaVotacion() {
            Votacion nueva = Votacion.builder()
                    .asambleaId(1L).titulo("Aprobacion presupuesto")
                    .tipoVotacion(TipoVotacion.APROBACION_SIMPLE).orden(1)
                    .opciones(List.of(
                            OpcionVoto.builder().nombre("Si").orden(1).build(),
                            OpcionVoto.builder().nombre("No").orden(2).build(),
                            OpcionVoto.builder().nombre("Abstencion").orden(3).build()
                    )).build();

            when(asambleaRepository.findById(1L)).thenReturn(Optional.of(testAsamblea));
            when(votacionRepository.save(any())).thenAnswer(inv -> {
                Votacion v = inv.getArgument(0);
                v.setId(1L);
                return v;
            });
            when(opcionVotoRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

            Votacion result = votacionService.create(nueva);

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getEstado()).isEqualTo(EstadoVotacion.PENDIENTE);
            verify(opcionVotoRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("No debe crear votacion en asamblea cerrada")
        void create_asambleaCerrada_lanzaExcepcion() {
            testAsamblea.setEstado(EstadoAsamblea.CERRADA);
            Votacion nueva = Votacion.builder().asambleaId(1L).titulo("Test").build();

            when(asambleaRepository.findById(1L)).thenReturn(Optional.of(testAsamblea));

            assertThatThrownBy(() -> votacionService.create(nueva))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("cerrada o cancelada");
        }

        @Test
        @DisplayName("Debe lanzar excepcion si asamblea no existe")
        void create_asambleaNoExiste_lanzaExcepcion() {
            Votacion nueva = Votacion.builder().asambleaId(99L).build();
            when(asambleaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> votacionService.create(nueva))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Open/Close Tests")
    class StateTests {

        @Test
        @DisplayName("Debe abrir votacion pendiente")
        void open_pendiente_abreVotacion() {
            when(votacionRepository.findById(1L)).thenReturn(Optional.of(testVotacion));
            when(opcionVotoRepository.findByVotacionId(1L)).thenReturn(List.of());
            when(votacionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Votacion result = votacionService.open(1L);

            assertThat(result.getEstado()).isEqualTo(EstadoVotacion.ABIERTA);
            assertThat(result.getFechaApertura()).isNotNull();
        }

        @Test
        @DisplayName("No debe abrir votacion ya abierta")
        void open_yaAbierta_lanzaExcepcion() {
            testVotacion.setEstado(EstadoVotacion.ABIERTA);
            when(votacionRepository.findById(1L)).thenReturn(Optional.of(testVotacion));
            when(opcionVotoRepository.findByVotacionId(1L)).thenReturn(List.of());

            assertThatThrownBy(() -> votacionService.open(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("PENDIENTE");
        }

        @Test
        @DisplayName("Debe cerrar votacion abierta")
        void close_abierta_cierraVotacion() {
            testVotacion.setEstado(EstadoVotacion.ABIERTA);
            when(votacionRepository.findById(1L)).thenReturn(Optional.of(testVotacion));
            when(opcionVotoRepository.findByVotacionId(1L)).thenReturn(List.of());
            when(votacionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Votacion result = votacionService.close(1L);

            assertThat(result.getEstado()).isEqualTo(EstadoVotacion.CERRADA);
            assertThat(result.getFechaCierre()).isNotNull();
        }

        @Test
        @DisplayName("No debe cerrar votacion pendiente")
        void close_pendiente_lanzaExcepcion() {
            when(votacionRepository.findById(1L)).thenReturn(Optional.of(testVotacion));
            when(opcionVotoRepository.findByVotacionId(1L)).thenReturn(List.of());

            assertThatThrownBy(() -> votacionService.close(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("ABIERTA");
        }
    }

    @Test
    @DisplayName("Debe obtener votacion por ID con opciones")
    void getById_existente_retornaConOpciones() {
        List<OpcionVoto> opciones = List.of(
                OpcionVoto.builder().id(1L).nombre("Si").build(),
                OpcionVoto.builder().id(2L).nombre("No").build()
        );
        when(votacionRepository.findById(1L)).thenReturn(Optional.of(testVotacion));
        when(opcionVotoRepository.findByVotacionId(1L)).thenReturn(opciones);

        Votacion result = votacionService.getById(1L);

        assertThat(result.getOpciones()).hasSize(2);
    }
}
