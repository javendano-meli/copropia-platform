package com.copropia.zonacomun.application.service;

import com.copropia.common.enums.EstadoReserva;
import com.copropia.common.enums.TipoReserva;
import com.copropia.common.exception.BusinessException;
import com.copropia.common.exception.ResourceNotFoundException;
import com.copropia.zonacomun.domain.model.Reserva;
import com.copropia.zonacomun.domain.model.ZonaComun;
import com.copropia.zonacomun.domain.port.out.ReservaRepository;
import com.copropia.zonacomun.domain.port.out.ZonaComunRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;
    @Mock
    private ZonaComunRepository zonaComunRepository;

    @InjectMocks
    private ReservaService reservaService;

    private ZonaComun zonaPorHoras;
    private ZonaComun zonaDiaCompleto;

    @BeforeEach
    void setUp() {
        zonaPorHoras = ZonaComun.builder()
                .id(1L).copropiedadId(1L).nombre("Salon Comunal")
                .tipoReserva(TipoReserva.POR_HORAS).maxHorasReserva(4)
                .horaApertura(LocalTime.of(8, 0)).horaCierre(LocalTime.of(22, 0))
                .activa(true).requiereAprobacion(false).build();

        zonaDiaCompleto = ZonaComun.builder()
                .id(2L).copropiedadId(1L).nombre("BBQ")
                .tipoReserva(TipoReserva.DIA_COMPLETO)
                .activa(true).requiereAprobacion(true).build();
    }

    @Nested
    @DisplayName("Create Reserva Tests")
    class CreateTests {

        @Test
        @DisplayName("Debe crear reserva por horas - confirmada automaticamente")
        void create_porHoras_creaConfirmada() {
            Reserva reserva = Reserva.builder()
                    .zonaComunId(1L).usuarioId(1L).propiedadId(1L)
                    .fecha(LocalDate.now().plusDays(1))
                    .horaInicio(LocalTime.of(10, 0)).horaFin(LocalTime.of(13, 0))
                    .build();

            when(zonaComunRepository.findById(1L)).thenReturn(Optional.of(zonaPorHoras));
            when(reservaRepository.existsOverlap(eq(1L), any(), any(), any())).thenReturn(false);
            when(reservaRepository.save(any())).thenAnswer(inv -> {
                Reserva r = inv.getArgument(0);
                r.setId(1L);
                return r;
            });

            Reserva result = reservaService.create(reserva);

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getEstado()).isEqualTo(EstadoReserva.CONFIRMADA);
        }

        @Test
        @DisplayName("Debe crear reserva dia completo - pendiente si requiere aprobacion")
        void create_diaCompleto_creaPendiente() {
            Reserva reserva = Reserva.builder()
                    .zonaComunId(2L).usuarioId(1L).propiedadId(1L)
                    .fecha(LocalDate.now().plusDays(3))
                    .build();

            when(zonaComunRepository.findById(2L)).thenReturn(Optional.of(zonaDiaCompleto));
            when(reservaRepository.existsOverlap(eq(2L), any(), any(), any())).thenReturn(false);
            when(reservaRepository.save(any())).thenAnswer(inv -> {
                Reserva r = inv.getArgument(0);
                r.setId(1L);
                return r;
            });

            Reserva result = reservaService.create(reserva);

            assertThat(result.getEstado()).isEqualTo(EstadoReserva.PENDIENTE);
            assertThat(result.getHoraInicio()).isEqualTo(LocalTime.of(0, 0));
            assertThat(result.getHoraFin()).isEqualTo(LocalTime.of(23, 59));
        }

        @Test
        @DisplayName("Debe rechazar reserva en zona inactiva")
        void create_zonaInactiva_lanzaExcepcion() {
            zonaPorHoras.setActiva(false);
            Reserva reserva = Reserva.builder().zonaComunId(1L).fecha(LocalDate.now().plusDays(1)).build();
            when(zonaComunRepository.findById(1L)).thenReturn(Optional.of(zonaPorHoras));

            assertThatThrownBy(() -> reservaService.create(reserva))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("no esta activa");
        }

        @Test
        @DisplayName("Debe rechazar reserva en fecha pasada")
        void create_fechaPasada_lanzaExcepcion() {
            Reserva reserva = Reserva.builder().zonaComunId(1L).fecha(LocalDate.now().minusDays(1)).build();
            when(zonaComunRepository.findById(1L)).thenReturn(Optional.of(zonaPorHoras));

            assertThatThrownBy(() -> reservaService.create(reserva))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("fecha pasada");
        }

        @Test
        @DisplayName("Debe rechazar reserva con conflicto de horario")
        void create_conflictoHorario_lanzaExcepcion() {
            Reserva reserva = Reserva.builder()
                    .zonaComunId(1L).usuarioId(1L).propiedadId(1L)
                    .fecha(LocalDate.now().plusDays(1))
                    .horaInicio(LocalTime.of(10, 0)).horaFin(LocalTime.of(12, 0))
                    .build();

            when(zonaComunRepository.findById(1L)).thenReturn(Optional.of(zonaPorHoras));
            when(reservaRepository.existsOverlap(eq(1L), any(), any(), any())).thenReturn(true);

            assertThatThrownBy(() -> reservaService.create(reserva))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Ya existe una reserva");
        }

        @Test
        @DisplayName("Debe rechazar reserva que excede max horas")
        void create_excedeMaxHoras_lanzaExcepcion() {
            Reserva reserva = Reserva.builder()
                    .zonaComunId(1L).usuarioId(1L).propiedadId(1L)
                    .fecha(LocalDate.now().plusDays(1))
                    .horaInicio(LocalTime.of(10, 0)).horaFin(LocalTime.of(16, 0))
                    .build();

            when(zonaComunRepository.findById(1L)).thenReturn(Optional.of(zonaPorHoras));

            assertThatThrownBy(() -> reservaService.create(reserva))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("excede el maximo");
        }

        @Test
        @DisplayName("Debe rechazar reserva fuera del horario de apertura")
        void create_fueraHorario_lanzaExcepcion() {
            Reserva reserva = Reserva.builder()
                    .zonaComunId(1L).usuarioId(1L).propiedadId(1L)
                    .fecha(LocalDate.now().plusDays(1))
                    .horaInicio(LocalTime.of(6, 0)).horaFin(LocalTime.of(9, 0))
                    .build();

            when(zonaComunRepository.findById(1L)).thenReturn(Optional.of(zonaPorHoras));

            assertThatThrownBy(() -> reservaService.create(reserva))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("apertura");
        }

        @Test
        @DisplayName("Debe rechazar reserva sin hora inicio/fin para por horas")
        void create_sinHoras_lanzaExcepcion() {
            Reserva reserva = Reserva.builder()
                    .zonaComunId(1L).usuarioId(1L).propiedadId(1L)
                    .fecha(LocalDate.now().plusDays(1))
                    .horaInicio(null).horaFin(null)
                    .build();

            when(zonaComunRepository.findById(1L)).thenReturn(Optional.of(zonaPorHoras));

            assertThatThrownBy(() -> reservaService.create(reserva))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("hora de inicio y fin");
        }
    }

    @Nested
    @DisplayName("Confirm/Cancel Tests")
    class StateTests {

        @Test
        @DisplayName("Debe confirmar reserva pendiente")
        void confirm_pendiente_confirma() {
            Reserva reserva = Reserva.builder().id(1L).estado(EstadoReserva.PENDIENTE).build();
            when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
            when(reservaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Reserva result = reservaService.confirm(1L);

            assertThat(result.getEstado()).isEqualTo(EstadoReserva.CONFIRMADA);
        }

        @Test
        @DisplayName("No debe confirmar reserva ya confirmada")
        void confirm_yaConfirmada_lanzaExcepcion() {
            Reserva reserva = Reserva.builder().id(1L).estado(EstadoReserva.CONFIRMADA).build();
            when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

            assertThatThrownBy(() -> reservaService.confirm(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("PENDIENTE");
        }

        @Test
        @DisplayName("Debe cancelar reserva confirmada")
        void cancel_confirmada_cancela() {
            Reserva reserva = Reserva.builder().id(1L).usuarioId(1L).estado(EstadoReserva.CONFIRMADA).build();
            when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
            when(reservaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Reserva result = reservaService.cancel(1L, 1L);

            assertThat(result.getEstado()).isEqualTo(EstadoReserva.CANCELADA);
        }

        @Test
        @DisplayName("No debe cancelar reserva ya cancelada")
        void cancel_yaCancelada_lanzaExcepcion() {
            Reserva reserva = Reserva.builder().id(1L).estado(EstadoReserva.CANCELADA).build();
            when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

            assertThatThrownBy(() -> reservaService.cancel(1L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("ya esta cancelada");
        }

        @Test
        @DisplayName("No debe cancelar reserva completada")
        void cancel_completada_lanzaExcepcion() {
            Reserva reserva = Reserva.builder().id(1L).estado(EstadoReserva.COMPLETADA).build();
            when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

            assertThatThrownBy(() -> reservaService.cancel(1L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("completada");
        }
    }

    @Test
    @DisplayName("Debe obtener reserva por ID")
    void getById_existente_retornaReserva() {
        Reserva reserva = Reserva.builder().id(1L).zonaComunId(1L).build();
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        assertThat(reservaService.getById(1L).getZonaComunId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando reserva no existe")
    void getById_noExiste_lanzaExcepcion() {
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservaService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Debe obtener reservas por zona y fecha")
    void getByZonaAndFecha_retornaLista() {
        LocalDate fecha = LocalDate.now().plusDays(1);
        when(reservaRepository.findByZonaComunIdAndFecha(1L, fecha)).thenReturn(List.of(
                Reserva.builder().id(1L).build(),
                Reserva.builder().id(2L).build()
        ));

        assertThat(reservaService.getByZonaComunIdAndFecha(1L, fecha)).hasSize(2);
    }
}
