package com.copropia.zonacomun.application.service;

import com.copropia.common.enums.TipoReserva;
import com.copropia.common.exception.BusinessException;
import com.copropia.common.exception.ResourceNotFoundException;
import com.copropia.zonacomun.domain.model.ZonaComun;
import com.copropia.zonacomun.domain.port.out.ZonaComunRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ZonaComunServiceTest {

    @Mock
    private ZonaComunRepository zonaComunRepository;

    @InjectMocks
    private ZonaComunService zonaComunService;

    @Nested
    @DisplayName("Create Tests")
    class CreateTests {

        @Test
        @DisplayName("Debe crear zona comun por horas exitosamente")
        void create_porHoras_creaZona() {
            ZonaComun zona = ZonaComun.builder()
                    .copropiedadId(1L).nombre("Salon Comunal").capacidad(50)
                    .tipoReserva(TipoReserva.POR_HORAS).maxHorasReserva(4)
                    .horaApertura(LocalTime.of(8, 0)).horaCierre(LocalTime.of(22, 0))
                    .build();

            when(zonaComunRepository.existsByCopropiedadIdAndNombre(1L, "Salon Comunal")).thenReturn(false);
            when(zonaComunRepository.save(any())).thenAnswer(inv -> {
                ZonaComun z = inv.getArgument(0);
                z.setId(1L);
                return z;
            });

            ZonaComun result = zonaComunService.create(zona);

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.isActiva()).isTrue();
            assertThat(result.getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Debe crear zona comun dia completo exitosamente")
        void create_diaCompleto_creaZona() {
            ZonaComun zona = ZonaComun.builder()
                    .copropiedadId(1L).nombre("BBQ").capacidad(20)
                    .tipoReserva(TipoReserva.DIA_COMPLETO)
                    .build();

            when(zonaComunRepository.existsByCopropiedadIdAndNombre(1L, "BBQ")).thenReturn(false);
            when(zonaComunRepository.save(any())).thenAnswer(inv -> {
                ZonaComun z = inv.getArgument(0);
                z.setId(2L);
                return z;
            });

            ZonaComun result = zonaComunService.create(zona);

            assertThat(result.getId()).isEqualTo(2L);
            assertThat(result.getTipoReserva()).isEqualTo(TipoReserva.DIA_COMPLETO);
        }

        @Test
        @DisplayName("Debe rechazar nombre duplicado en misma copropiedad")
        void create_nombreDuplicado_lanzaExcepcion() {
            ZonaComun zona = ZonaComun.builder().copropiedadId(1L).nombre("Piscina").build();
            when(zonaComunRepository.existsByCopropiedadIdAndNombre(1L, "Piscina")).thenReturn(true);

            assertThatThrownBy(() -> zonaComunService.create(zona))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Ya existe");
        }

        @Test
        @DisplayName("Debe rechazar por horas sin max horas")
        void create_porHorasSinMax_lanzaExcepcion() {
            ZonaComun zona = ZonaComun.builder()
                    .copropiedadId(1L).nombre("Gym").tipoReserva(TipoReserva.POR_HORAS)
                    .maxHorasReserva(null)
                    .horaApertura(LocalTime.of(6, 0)).horaCierre(LocalTime.of(22, 0))
                    .build();
            when(zonaComunRepository.existsByCopropiedadIdAndNombre(1L, "Gym")).thenReturn(false);

            assertThatThrownBy(() -> zonaComunService.create(zona))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("maximo de horas");
        }

        @Test
        @DisplayName("Debe rechazar por horas sin horario apertura/cierre")
        void create_porHorasSinHorario_lanzaExcepcion() {
            ZonaComun zona = ZonaComun.builder()
                    .copropiedadId(1L).nombre("Gym").tipoReserva(TipoReserva.POR_HORAS)
                    .maxHorasReserva(2)
                    .horaApertura(null).horaCierre(null)
                    .build();
            when(zonaComunRepository.existsByCopropiedadIdAndNombre(1L, "Gym")).thenReturn(false);

            assertThatThrownBy(() -> zonaComunService.create(zona))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("horario");
        }

        @Test
        @DisplayName("Debe rechazar apertura posterior a cierre")
        void create_aperturaPosteriaCierre_lanzaExcepcion() {
            ZonaComun zona = ZonaComun.builder()
                    .copropiedadId(1L).nombre("Gym").tipoReserva(TipoReserva.POR_HORAS)
                    .maxHorasReserva(2)
                    .horaApertura(LocalTime.of(22, 0)).horaCierre(LocalTime.of(8, 0))
                    .build();
            when(zonaComunRepository.existsByCopropiedadIdAndNombre(1L, "Gym")).thenReturn(false);

            assertThatThrownBy(() -> zonaComunService.create(zona))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("anterior");
        }
    }

    @Test
    @DisplayName("Debe obtener zona por ID")
    void getById_existente_retornaZona() {
        ZonaComun zona = ZonaComun.builder().id(1L).nombre("Piscina").build();
        when(zonaComunRepository.findById(1L)).thenReturn(Optional.of(zona));

        assertThat(zonaComunService.getById(1L).getNombre()).isEqualTo("Piscina");
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando zona no existe")
    void getById_noExiste_lanzaExcepcion() {
        when(zonaComunRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> zonaComunService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Debe obtener zonas por copropiedad")
    void getByCopropiedadId_retornaLista() {
        when(zonaComunRepository.findByCopropiedadId(1L)).thenReturn(List.of(
                ZonaComun.builder().id(1L).nombre("Piscina").build(),
                ZonaComun.builder().id(2L).nombre("BBQ").build()
        ));

        assertThat(zonaComunService.getByCopropiedadId(1L)).hasSize(2);
    }

    @Test
    @DisplayName("Debe desactivar zona comun")
    void deactivate_desactivaZona() {
        ZonaComun zona = ZonaComun.builder().id(1L).nombre("Piscina").activa(true).build();
        when(zonaComunRepository.findById(1L)).thenReturn(Optional.of(zona));
        when(zonaComunRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        zonaComunService.deactivate(1L);

        assertThat(zona.isActiva()).isFalse();
    }
}
