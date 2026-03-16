package com.copropia.asamblea.application.service;

import com.copropia.asamblea.domain.model.Asamblea;
import com.copropia.asamblea.domain.port.out.AsambleaRepository;
import com.copropia.common.enums.EstadoAsamblea;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsambleaServiceTest {

    @Mock
    private AsambleaRepository asambleaRepository;

    @InjectMocks
    private AsambleaService asambleaService;

    private Asamblea testAsamblea;

    @BeforeEach
    void setUp() {
        testAsamblea = Asamblea.builder()
                .id(1L).copropiedadId(1L).creadoPor(1L)
                .nombre("Asamblea Ordinaria 2026")
                .fechaProgramada(LocalDateTime.of(2026, 3, 20, 14, 0))
                .estado(EstadoAsamblea.PROGRAMADA)
                .quorumRequerido(new BigDecimal("51.00"))
                .build();
    }

    @Nested
    @DisplayName("Create Tests")
    class CreateTests {

        @Test
        @DisplayName("Debe crear asamblea con estado PROGRAMADA")
        void create_datosValidos_creaConEstadoProgramada() {
            Asamblea nueva = Asamblea.builder()
                    .copropiedadId(1L).creadoPor(1L).nombre("Asamblea Extraordinaria")
                    .fechaProgramada(LocalDateTime.now().plusDays(7))
                    .quorumRequerido(new BigDecimal("51.00")).build();

            when(asambleaRepository.save(any())).thenAnswer(inv -> {
                Asamblea a = inv.getArgument(0);
                a.setId(2L);
                return a;
            });

            Asamblea result = asambleaService.create(nueva);

            assertThat(result.getId()).isEqualTo(2L);
            assertThat(result.getEstado()).isEqualTo(EstadoAsamblea.PROGRAMADA);
            assertThat(result.getCreatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Open Tests")
    class OpenTests {

        @Test
        @DisplayName("Debe abrir asamblea programada")
        void open_programada_abreAsamblea() {
            when(asambleaRepository.findById(1L)).thenReturn(Optional.of(testAsamblea));
            when(asambleaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Asamblea result = asambleaService.open(1L);

            assertThat(result.getEstado()).isEqualTo(EstadoAsamblea.ABIERTA);
        }

        @Test
        @DisplayName("No debe abrir asamblea ya abierta")
        void open_yaAbierta_lanzaExcepcion() {
            testAsamblea.setEstado(EstadoAsamblea.ABIERTA);
            when(asambleaRepository.findById(1L)).thenReturn(Optional.of(testAsamblea));

            assertThatThrownBy(() -> asambleaService.open(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("PROGRAMADA");
        }

        @Test
        @DisplayName("No debe abrir asamblea cerrada")
        void open_cerrada_lanzaExcepcion() {
            testAsamblea.setEstado(EstadoAsamblea.CERRADA);
            when(asambleaRepository.findById(1L)).thenReturn(Optional.of(testAsamblea));

            assertThatThrownBy(() -> asambleaService.open(1L))
                    .isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("Close Tests")
    class CloseTests {

        @Test
        @DisplayName("Debe cerrar asamblea abierta")
        void close_abierta_cierraAsamblea() {
            testAsamblea.setEstado(EstadoAsamblea.ABIERTA);
            when(asambleaRepository.findById(1L)).thenReturn(Optional.of(testAsamblea));
            when(asambleaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Asamblea result = asambleaService.close(1L);

            assertThat(result.getEstado()).isEqualTo(EstadoAsamblea.CERRADA);
        }

        @Test
        @DisplayName("No debe cerrar asamblea programada")
        void close_programada_lanzaExcepcion() {
            when(asambleaRepository.findById(1L)).thenReturn(Optional.of(testAsamblea));

            assertThatThrownBy(() -> asambleaService.close(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("ABIERTA");
        }
    }

    @Test
    @DisplayName("Debe obtener asamblea por ID")
    void getById_existente_retornaAsamblea() {
        when(asambleaRepository.findById(1L)).thenReturn(Optional.of(testAsamblea));

        assertThat(asambleaService.getById(1L).getNombre()).isEqualTo("Asamblea Ordinaria 2026");
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando asamblea no existe")
    void getById_noExiste_lanzaExcepcion() {
        when(asambleaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> asambleaService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Debe obtener asambleas por copropiedad")
    void getByCopropiedadId_retornaLista() {
        when(asambleaRepository.findByCopropiedadId(1L)).thenReturn(List.of(testAsamblea));

        assertThat(asambleaService.getByCopropiedadId(1L)).hasSize(1);
    }
}
