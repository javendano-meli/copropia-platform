package com.copropia.copropiedad.application.service;

import com.copropia.common.enums.EstadoCopropiedad;
import com.copropia.copropiedad.domain.model.Copropiedad;
import com.copropia.copropiedad.domain.model.Plan;
import com.copropia.copropiedad.domain.port.out.CopropiedadRepository;
import com.copropia.copropiedad.domain.port.out.PlanRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CopropiedadServiceTest {

    @Mock
    private CopropiedadRepository copropiedadRepository;
    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private CopropiedadService copropiedadService;

    private Copropiedad testCopropiedad;

    @BeforeEach
    void setUp() {
        testCopropiedad = Copropiedad.builder()
                .id(1L).planId(1L).nombre("Torres del Parque")
                .nit("900123456-1").direccion("Cra 5 #26-40")
                .ciudad("Bogota").estado(EstadoCopropiedad.ACTIVA)
                .build();
    }

    @Nested
    @DisplayName("Create Tests")
    class CreateTests {

        @Test
        @DisplayName("Debe crear copropiedad exitosamente")
        void create_datosValidos_creaCopropiedad() {
            Copropiedad nueva = Copropiedad.builder().planId(1L).nombre("Nuevo Conjunto").nit("900999999-1")
                    .direccion("Calle 100").ciudad("Bogota").build();

            when(copropiedadRepository.existsByNit("900999999-1")).thenReturn(false);
            when(planRepository.findById(1L)).thenReturn(Optional.of(Plan.builder().id(1L).build()));
            when(copropiedadRepository.save(any())).thenAnswer(inv -> {
                Copropiedad c = inv.getArgument(0);
                c.setId(2L);
                return c;
            });

            Copropiedad result = copropiedadService.create(nueva);

            assertThat(result.getId()).isEqualTo(2L);
            assertThat(result.getEstado()).isEqualTo(EstadoCopropiedad.ACTIVA);
            assertThat(result.getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Debe rechazar NIT duplicado")
        void create_nitDuplicado_lanzaExcepcion() {
            Copropiedad nueva = Copropiedad.builder().nit("900123456-1").build();
            when(copropiedadRepository.existsByNit("900123456-1")).thenReturn(true);

            assertThatThrownBy(() -> copropiedadService.create(nueva))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("NIT");

            verify(copropiedadRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe rechazar plan inexistente")
        void create_planNoExiste_lanzaExcepcion() {
            Copropiedad nueva = Copropiedad.builder().planId(99L).nit("900888888-1").build();
            when(copropiedadRepository.existsByNit("900888888-1")).thenReturn(false);
            when(planRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> copropiedadService.create(nueva))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Test
    @DisplayName("Debe obtener copropiedad por ID")
    void getById_existente_retornaCopropiedad() {
        when(copropiedadRepository.findById(1L)).thenReturn(Optional.of(testCopropiedad));

        Copropiedad result = copropiedadService.getById(1L);

        assertThat(result.getNombre()).isEqualTo("Torres del Parque");
    }

    @Test
    @DisplayName("Debe retornar todas las copropiedades")
    void getAll_retornaLista() {
        when(copropiedadRepository.findAll()).thenReturn(List.of(testCopropiedad));

        assertThat(copropiedadService.getAll()).hasSize(1);
    }

    @Test
    @DisplayName("Debe desactivar copropiedad")
    void deactivate_copropiedadActiva_desactiva() {
        when(copropiedadRepository.findById(1L)).thenReturn(Optional.of(testCopropiedad));
        when(copropiedadRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        copropiedadService.deactivate(1L);

        assertThat(testCopropiedad.getEstado()).isEqualTo(EstadoCopropiedad.INACTIVA);
    }
}
